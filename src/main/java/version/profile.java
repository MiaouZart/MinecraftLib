package version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class profile {


    private String m_version ; //version de minecraft

    //gestion des fichier//
    private String m_path;     //Path vers les fichier de la version
    private String m_pathAssets ;  //Path vers les assests de la version
    private String m_pathNative ;  //Path vers les Native
    private String m_libraries;
    //
    private String m_json;         //json de la version dl sur le site piston

    public profile(String version , String path , String jsonUrl){
        m_version = version;
        m_path =path;
        m_pathAssets = path+"/assets";
        m_pathNative = path+"/natives";
        m_libraries = path+"/libraries";
        createDir();
    }


    private void createDir(){
        try {
            Files.createDirectories(Paths.get(m_path));
            Files.createDirectories(Paths.get(m_libraries));
            Files.createDirectories(Paths.get(m_pathAssets ));
            Files.createDirectories(Paths.get(m_pathNative));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
