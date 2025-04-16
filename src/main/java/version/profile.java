package version;

import User.User;
import common.global;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static common.fileManager.downloadFile;
import static common.fileManager.downloadText;
import static common.global.VersionAPI.VANILLA_MANIFEST;
import static common.manifestFinder.findManifest;

public class profile {


    private String m_version; //version de minecraft

    //gestion des fichier//
    protected String m_path;     //Path vers les fichier de la version
    protected String m_pathAssets;  //Path vers les assests de la version
    protected String m_pathNative;  //Path vers les Native
    protected String m_pathlibraries;    //Path vers les libraries
    //
    private String m_jsonUrl;         //url du json de la version dl sur le site piston
    private JSONObject m_jsonVersion; //Json object de la version
    private String m_assetIndex;

    private ArrayList<String> m_classPathList;
    private ProcessBuilder m_mcProcess;

    private  String m_classpath;
    private  List<String>  m_command;

    public profile(String version, String path) {
        m_version = version;
        m_path = path;
        m_pathAssets = path + "/assets";
        m_pathNative = path + "/natives";
        m_pathlibraries = path + "/libraries";
        m_classPathList = new ArrayList<String>();
        try {
            createDir();
            m_jsonUrl = findManifest(version, VANILLA_MANIFEST.getUrl());
            m_jsonVersion = new JSONObject(downloadText(m_jsonUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(m_jsonVersion.getString("mainClass"));


    }


    protected void addToClassPathList(String path){
        if(path.isEmpty())return;
        m_classPathList.add(path);
    }


    private void createDir() {
        try {
            Files.createDirectories(Paths.get(m_path));
            Files.createDirectories(Paths.get(m_pathlibraries));
            Files.createDirectories(Paths.get(m_pathAssets));
            Files.createDirectories(Paths.get(m_pathNative));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void downloadLib() throws IOException {
        JSONArray libraries = m_jsonVersion.getJSONArray("libraries");//on récup les libs du json
        for (int i = 0; i < libraries.length(); i++) {
            JSONObject lib = libraries.getJSONObject(i);//on récup la lib a l'indice i
            if (!lib.has("downloads")) continue;//si pas de dl dans le json alors on skip
            JSONObject downloads = lib.getJSONObject("downloads");

            if (downloads.has("artifact")) {
                JSONObject artifact = downloads.getJSONObject("artifact");
                String url = artifact.getString("url");
                String path = artifact.getString("path");
                File libFile = new File(m_pathlibraries, path);
                if (!libFile.exists()) {
                    System.out.println("📦 [Minecraft] Téléchargement : " + path);
                    libFile.getParentFile().mkdirs();
                    downloadFile(url, libFile);
                }
                m_classPathList.add(libFile.getAbsolutePath());
            }

        }
    }

    private void downloadClient() throws IOException {
        JSONObject client = m_jsonVersion.getJSONObject("downloads").getJSONObject("client");
        File clientJar = new File(m_path + "/" + m_version + ".jar");
        if (!clientJar.exists()) {
            System.out.println("🎮 Téléchargement du client...");
            downloadFile(client.getString("url"), clientJar);
        }
        m_classPathList.add(clientJar.getAbsolutePath());
    }

    private void downloadAssets() throws IOException {
        JSONObject assetIndex = m_jsonVersion.getJSONObject("assetIndex");
        m_assetIndex = assetIndex.getString("id");
        String assetUrl = assetIndex.getString("url");
        File assetIndexFile = new File(m_pathAssets + "/indexes/" + m_assetIndex + ".json");
        if (!assetIndexFile.exists()) {
            System.out.println("📁 Téléchargement de l'asset index...");
            assetIndexFile.getParentFile().mkdirs();
            downloadFile(assetUrl, assetIndexFile);
        }
        JSONObject objects = new JSONObject(Files.readString(assetIndexFile.toPath())).getJSONObject("objects");
        for (String key : objects.keySet()) {
            JSONObject obj = objects.getJSONObject(key);
            String hash = obj.getString("hash");
            String subdir = hash.substring(0, 2);
            File assetFile = new File(m_pathAssets + "/objects/" + subdir + "/" + hash);
            if (!assetFile.exists()) {
                String assetFileUrl = "https://resources.download.minecraft.net/" + subdir + "/" + hash;
                assetFile.getParentFile().mkdirs();
                System.out.println("🎨 Asset: " + key);
                downloadFile(assetFileUrl, assetFile);
            }
        }


    }

    private void buildCommand(User user){
        m_classpath = String.join(File.pathSeparator, m_classPathList);//build des classpath
        m_command = new ArrayList<>();
        m_command.add("java");
        m_command.add("-Xmx2G");
        m_command.add("-Djava.library.path=" + m_pathNative);
        m_command.add("-cp");
        m_command.add(m_classpath);
        m_command.add(m_jsonVersion.getString("mainClass"));
        m_command.add("--username");
        m_command.add(user.getUserName());
        m_command.add("--accessToken");
        m_command.add(user.getAccessToken());
        m_command.add("--version");
        m_command.add(m_version);
        m_command.add("--gameDir");
        m_command.add(m_path);
        m_command.add("--assetsDir");
        m_command.add(m_pathAssets);
        m_command.add("--assetIndex");
        m_command.add(m_assetIndex);
        m_command.add("--uuid");
        m_command.add(user.getUUID());
        m_command.add("--userType");
        m_command.add(user.getUserType());

    }

    private void buildProcess(){
        m_mcProcess =new ProcessBuilder(m_command).inheritIO();
    }

    public void StartProfile(User user){
        try {
            downloadLib();
            downloadClient();
            downloadAssets();
            buildCommand(user);
            buildProcess();
            m_mcProcess.start();
        } catch (IOException e) {
            System.out.println("Probleme");

            throw new RuntimeException(e);
        }
    }

}
