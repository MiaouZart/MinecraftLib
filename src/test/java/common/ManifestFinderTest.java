package common;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static common.manifestFinder.findManifest;
import static org.junit.jupiter.api.Assertions.*;

public class ManifestFinderTest {
    @Test
    void test1_21_5() throws IOException {
        String url1_21_5 = "https://piston-meta.mojang.com/v1/packages/e81f971b7c3ebcebb00e028eb7d33fad23c266a2/1.21.5.json";
        String urlcreated = findManifest("1.21.5", global.VersionAPI.VANILLA_MANIFEST.getUrl());
        assertEquals(url1_21_5, urlcreated);
    }

    @Test
    void testNotFind() {
        assertThrows(IllegalArgumentException.class, () -> {
            findManifest("2.0.0",global.VersionAPI.VANILLA_MANIFEST.getUrl());
        });
    }


}