package com.example.swinecore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

/**
 * Handles saving and deleting uploaded files.
 *
 * Upload validation:
 *  - Allowed MIME types: image/jpeg, image/png, image/webp, image/gif
 *  - Max file size is enforced by spring.servlet.multipart.max-file-size (10 MB)
 *  - Extension is derived from the declared content-type, not the original filename,
 *    to prevent extension-spoofing attacks.
 */
@Component
public class FileUploadUtil {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /** Allowed MIME types for profile images */
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    /**
     * Save a multipart file to [uploadDir]/[subFolder]/[uuid].[ext]
     * Returns the URL-relative path for DB storage (/uploads/...).
     *
     * @throws IllegalArgumentException if the content type is not an allowed image type
     * @throws IOException              on filesystem errors
     */
    public String saveFile(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "Unsupported file type. Only JPEG, PNG, WebP, and GIF images are allowed."
            );
        }

        // Derive extension from validated MIME type (ignores original filename)
        String ext = mimeToExtension(contentType.toLowerCase());
        String fileName = UUID.randomUUID() + ext;

        Path targetDir = Paths.get(uploadDir, subFolder);
        Files.createDirectories(targetDir);

        Path targetPath = targetDir.resolve(fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + subFolder + "/" + fileName;
    }

    /** Delete a previously stored file given its relative path (e.g. /uploads/users/uuid.jpg). */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            String filePart = relativePath.replaceFirst("^/uploads/", "");
            Path path = Paths.get(uploadDir, filePart);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {}
    }

    private String mimeToExtension(String mime) {
        return switch (mime) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default           -> ".bin"; // should never reach here due to validation above
        };
    }
}
