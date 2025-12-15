package com.example.piuda.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Fallback no-op implementation used when NHN storage properties are not set.
 * Returns placeholder URLs so application can still run.
 */
public class NoopStorageService implements StorageService {
    @Override
    public String upload(String folder, MultipartFile file) {
        return "noop://" + folder + "/" + (file != null ? file.getOriginalFilename() : "null");
    }

    @Override
    public void delete(String key) {
        // no-op
    }

    @Override
    public String getUrl(String key) {
        return "noop://" + key;
    }

    @Override
    public String getPresignedUrl(String key, int expirationMinutes) {
        return "noop://" + key + "?presigned=true&expires=" + expirationMinutes;
    }
}
