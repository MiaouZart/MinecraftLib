package common;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static common.manifestFinder.findManifest;
import static org.junit.jupiter.api.Assertions.*;

public class ManifestFinderTest {
    @Test
    void test1_21_5() throws IOException {
        String url1_21_5 = "";
        String urlcreated = findManifest("1.21.5");
        assertEquals(url1_21_5, urlcreated);
    }

    @Test
    void testNotFind() {
        assertThrows(IllegalArgumentException.class, () -> {
            findManifest("2.0.0");
        });
    }
}