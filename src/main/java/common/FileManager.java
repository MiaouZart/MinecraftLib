package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {

    public static String downloadText(String urlStr) throws IOException {
        try (InputStream in = new URL(urlStr).openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void downloadFile(String urlStr, File output) throws IOException {
        try (InputStream in = new URL(urlStr).openStream()) {
            Files.copy(in, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void extractZip(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory() || !entry.getName().endsWith(".dll")) continue;
                File outFile = new File(destDir, entry.getName());
                outFile.getParentFile().mkdirs();
                Files.copy(zis, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

}