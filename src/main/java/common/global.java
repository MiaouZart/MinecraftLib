package common;

public class global {

    public enum VersionAPI {
        VANILLA_MANIFEST("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"),
        FABRIC_API("https://meta.fabricmc.net/"),
        FABRIC_LOADER_API("https://meta.fabricmc.net/v2/versions/loader/");//+version
        private final String m_url;
        VersionAPI(String url) {
            this.m_url = url;
        }
        public  String getUrl(){
            return m_url;
        }
    }
    public enum Auth{
        authenticateEndpoint("https://authserver.mojang.com/authenticate");
        private final String m_enpoint;
        Auth(String enpoint){
            m_enpoint =enpoint;
        }
        public String getM_enpoint(){
            return m_enpoint;
        }

    }
}
