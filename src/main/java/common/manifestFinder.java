package common;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

import static common.fileManager.downloadText;
import static common.global.VersionAPI.VANILLA_MANIFEST;

public class manifestFinder {
    /*
     *Find the correct jsonUrl for the given mc-version
     */
    public static String findManifest(String version) throws IOException,IllegalArgumentException {
        JSONObject minecraftManifest = new JSONObject(downloadText(VANILLA_MANIFEST.getUrl()));
        JSONArray versions = minecraftManifest.getJSONArray("versions");
        for (int i = 0; i < versions.length(); i++) {
            JSONObject version_i = versions.getJSONObject(i);
            if(Objects.equals(version_i.getString("id"), version)){
                return version_i.getString("url");
            }
        }
        throw new IllegalArgumentException("Version doesnt Exit");
    }

}
