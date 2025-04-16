package common;

import java.io.IOException;

import static common.manifestFinder.findManifest;
import static org.junit.jupiter.api.Assertions.*;

class manifestFinderTest {

    void test1_21_5() throws IOException {
        String url1_21_5 = ""; // Valeur attendue
        String urlcreated = findManifest("1.21.5");
        assertEquals(url1_21_5, urlcreated);
    }

    void testNotFind() {
        assertThrows(IllegalArgumentException.class, () -> {
            findManifest("2.0.0");
        });

    }
}