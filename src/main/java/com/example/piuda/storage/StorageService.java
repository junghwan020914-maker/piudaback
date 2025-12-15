package com.example.piuda.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Upload file under folder (string) and return public URL.
     */
    String upload(String folder, MultipartFile file);

    /**
     * Upload using predefined category enum.
     */
    default String upload(StorageFolder folder, MultipartFile file) {
        return upload(folder.getFolderName(), file);
    }

    /**
     * Delete an object by its key.
     */
    void delete(String key);

    /**
     * Build public URL for an object key.
     */
    String getUrl(String key);

    /**
     * Generate presigned URL for private objects.
     * @param key object key
     * @param expirationMinutes expiration time in minutes
     * @return presigned URL
     */
    String getPresignedUrl(String key, int expirationMinutes);
}
