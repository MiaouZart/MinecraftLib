package common;

public class global {

    public enum VersionAPI {
        VANILLA_MANIFEST("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"),
        FABRIC_API("https://meta.fabricmc.net/"),
        FABRIC_LOADER_API("https://meta.fabricmc.net/v2/versions/loader/");
        private final String m_url;
        VersionAPI(String url) {
            this.m_url = url;
        }
        public  String getUrl(){
            return m_url;
        }
    }
}
