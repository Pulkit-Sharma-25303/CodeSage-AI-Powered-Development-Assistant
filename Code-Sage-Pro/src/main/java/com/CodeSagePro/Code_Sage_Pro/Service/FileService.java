package com.CodeSagePro.Code_Sage_Pro.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipInputStream;

@Service
public class FileService {

    /**
     * Decompresses an uploaded ZIP file into a temporary directory.
     * @param zipFile The MultipartFile representing the uploaded .zip archive.
     * @return The path to the newly created temporary directory containing the decompressed project.
     * @throws IOException if a file I/O error occurs.
     */
    public Path decompress(MultipartFile zipFile) throws IOException {
        // 1. Create a temporary directory to store the decompressed files
        Path tempDir = Files.createTempDirectory("codesage-project-");

        // 2. Use a ZipInputStream to read the contents of the uploaded file
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            var zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                Path newPath = resolveZipEntryPath(tempDir, zipEntry.getName());

                // 3. Prevent Zip Slip vulnerability
                if (!newPath.normalize().startsWith(tempDir.normalize())) {
                    throw new IOException("Bad zip entry: " + zipEntry.getName());
                }

                // 4. If it's a directory, create it
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    // If it's a file, ensure parent directory exists and copy the file
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        return tempDir;
    }

    private Path resolveZipEntryPath(Path rootDir, String entryName) {
        // Resolve the file path, sanitizing the entry name
        return rootDir.resolve(entryName.replace("..", ""));
    }
}