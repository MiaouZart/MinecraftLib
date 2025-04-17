package common;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

import static common.FileManager.downloadText;

public class ManifestFinder {
    /*
     *Find the correct jsonUrl for the given mc-version
     */
    public static String findManifest(String version, String API) throws IOException,IllegalArgumentException {
        JSONObject minecraftManifest = new JSONObject(downloadText(API));
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
