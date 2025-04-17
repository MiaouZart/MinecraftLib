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
    private Map<String, String> m_libVersions = new HashMap<>();

    public fabric(String version, String path, String fabricVersion) {
        super(version, path);
        try {
            m_jsonUrl = FABRIC_LOADER_API.getUrl() + version + "/" + fabricVersion + "/profile/json";
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
        for (int i = 0; i < fabriclibraries.length(); i++) {
            JSONObject lib = fabriclibraries.getJSONObject(i);
            String name = lib.getString("name");
            String[] parts = name.split(":");
            if (parts.length != 3) continue;
            String group = parts[0];
            String artifact = parts[1];
            String version = parts[2];
            String libKey = group + ":" + artifact;
            if (!m_libVersions.containsKey(libKey)){
                m_libVersions.put(libKey, version);
            } else {
                String currentVersion = m_libVersions.get(libKey);
                if (compareVersions(version, currentVersion) > 0) {
                    m_libVersions.put(libKey, version);
                }
            }
        }
        for (int i = 0; i < fabriclibraries.length(); i++) {
            JSONObject lib = fabriclibraries.getJSONObject(i);
            String name = lib.getString("name");
            String[] parts = name.split(":");
            if (parts.length != 3) continue;

            String group = parts[0];
            String artifact = parts[1];
            String version = parts[2];
            String libKey = group + ":" + artifact;
            if (!version.equals(m_libVersions.get(libKey))) {
                continue;
            }
            String mavenGroup = group.replace('.', '/');
            String jarName = artifact + "-" + version + ".jar";
            String path = mavenGroup + "/" + artifact + "/" + version + "/" + jarName;
            String url = "https://maven.fabricmc.net/" + path;
            File libFile = new File(m_pathlibraries, path);
            if (!libFile.exists()) {
                System.out.println("📦 [Fabric] Téléchargement : " + path);
                libFile.getParentFile().mkdirs();
                downloadFile(url, libFile);
            }
            File artifactDir = new File(m_pathlibraries, mavenGroup + "/" + artifact);
            if (artifactDir.exists()) {
                File[] versionDirs = artifactDir.listFiles();
                if (versionDirs != null) {
                    for (File versionDir : versionDirs) {
                        if (!versionDir.getName().equals(version)) {
                            File oldJar = new File(versionDir, artifact + "-" + versionDir.getName() + ".jar");
                            if (oldJar.exists()) {
                                System.out.println("🗑️ [Fabric] Suppression ancienne version: " + oldJar.getPath());
                                oldJar.delete();
                                removeToClassPathList(oldJar.getAbsolutePath());
                                if (versionDir.list().length == 0) {
                                    versionDir.delete();
                                }
                            }
                        }
                    }
                }
            }

            addToClassPathList(libFile.getAbsolutePath());
        }
        setmainClass(m_jsonFabric.getString("mainClass"));
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }
}