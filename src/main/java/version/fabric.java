package version;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static common.fileManager.downloadFile;
import static common.fileManager.downloadText;
import static common.global.VersionAPI.FABRIC_LOADER_API;
import static common.manifestFinder.findManifest;

public class fabric extends profile {
    private String m_jsonUrl;
    private JSONObject m_jsonFabric;

    public fabric(String version, String path,String fabricVersion) {
        super(version, path);
        try {
            m_jsonUrl = FABRIC_LOADER_API.getUrl()+version+"/"+fabricVersion+"/profile/json";
            System.out.println(m_jsonUrl);
            m_jsonFabric = new JSONObject(downloadText(m_jsonUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void downloadLib() throws IOException {
        super.downloadLib();
        JSONArray fabriclibraries = m_jsonFabric.getJSONArray("libraries");
        Map<String, File> latestLibs = new HashMap<>();
        for (int i = 0; i < fabriclibraries.length(); i++) {
            JSONObject lib = fabriclibraries.getJSONObject(i);
            String name = lib.getString("name");
            String[] parts = name.split(":");
            if (parts.length != 3) continue;
            String group = parts[0].replace('.', '/');
            String artifact = parts[1];
            String version = parts[2];
            String libKey = group + ":" + artifact;
            String jarName = artifact + "-" + version + ".jar";
            String relativePath = group + "/" + artifact + "/" + version + "/" + jarName;
            File libFile = new File(m_pathlibraries, relativePath);
            if (latestLibs.containsKey(libKey)) {
                String existingVersion = latestLibs.get(libKey).getParentFile().getName();
                if (compareVersions(version, existingVersion) > 0) {
                    latestLibs.put(libKey, libFile);
                }
            } else {
                latestLibs.put(libKey, libFile);
            }
        }
        for (int i = 0; i < fabriclibraries.length(); i++) {
            JSONObject lib = fabriclibraries.getJSONObject(i);
            String name = lib.getString("name");
            String[] parts = name.split(":");
            if (parts.length != 3) continue;
            String group = parts[0].replace('.', '/');
            String artifact = parts[1];
            String version = parts[2];
            String libKey = group + ":" + artifact;
            String jarName = artifact + "-" + version + ".jar";
            String relativePath = group + "/" + artifact + "/" + version + "/" + jarName;
            String url = "https://maven.fabricmc.net/" + relativePath;
            File libFile = latestLibs.get(libKey);
            if (libFile.getAbsolutePath().endsWith(relativePath)) {
                if (!libFile.exists()) {
                    System.out.println("📦 [Fabric] Téléchargement : " + relativePath);
                    libFile.getParentFile().mkdirs();
                    downloadFile(url, libFile);
                }
                addToClassPathList(libFile.getAbsolutePath());

                File libDir = new File(m_pathlibraries, group + "/" + artifact);
                if (libDir.exists() && libDir.isDirectory()) {
                    File[] versionDirs = libDir.listFiles();
                    if (versionDirs != null) {
                        for (File verDir : versionDirs) {
                            if (!verDir.getName().equals(version)) {
                                File oldJar = new File(verDir, artifact + "-" + verDir.getName() + ".jar");
                                if (oldJar.exists()) {
                                    System.out.println("🗑️ [Fabric] Suppression ancienne version: " + oldJar.getPath());
                                    oldJar.delete();
                                    File[] remainingFiles = verDir.listFiles();
                                    if (remainingFiles != null && remainingFiles.length == 0) {
                                        verDir.delete();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        setmainClass(m_jsonFabric.getString("mainClass"));
    }

    /**
     * Compare deux versions au format semver (simplifié)
     * @param v1 Première version à comparer
     * @param v2 Deuxième version à comparer
     * @return 1 si v1 > v2, -1 si v1 < v2, 0 si égales
     */
    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int part1 = i < parts1.length ? tryParseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? tryParseInt(parts2[i]) : 0;

            if (part1 != part2) {
                return Integer.compare(part1, part2);
            }
        }
        return 0;
    }

    /**
     * Tente de parser un entier, retourne 0 en cas d'échec
     */
    private int tryParseInt(String value) {
        try {
            // Enlève les suffixes non numériques (ex: "1-beta" -> "1")
            String numOnly = value.replaceAll("[^0-9]", "");
            return numOnly.isEmpty() ? 0 : Integer.parseInt(numOnly);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}