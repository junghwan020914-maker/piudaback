package com.example.piuda.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties properties;

    @Override
    public String upload(String folder, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("빈 파일은 업로드할 수 없습니다.");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String prefix = properties.getKeyPrefix();
        if (prefix != null && !prefix.isBlank()) {
            prefix = prefix.replaceAll("^/+|/+$", ""); // trim leading/trailing slashes
            prefix = prefix + "/"; // ensure trailing slash once
        } else {
            prefix = "";
        }
        String key = prefix + folder + "/" + timestamp + "_" + UUID.randomUUID().toString().substring(0,8) + ext;
        try {
            PutObjectRequest.Builder builder = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
            if (properties.isPublicRead()) {
                builder.acl(ObjectCannedACL.PUBLIC_READ);
            }
            PutObjectRequest req = builder.build();
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new StorageException("파일 읽기 실패", e);
        } catch (S3Exception e) {
            throw new StorageException("S3 업로드 실패: " + e.awsErrorDetails().errorMessage(), e);
        }
        return getUrl(key);
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            throw new StorageException("S3 삭제 실패: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public String getUrl(String key) {
        if (properties.getPublicBaseUrl() != null && !properties.getPublicBaseUrl().isBlank()) {
            return properties.getPublicBaseUrl().replaceAll("/+$","") + "/" + key;
        }
        // path-style
        if (properties.isPathStyle()) {
            return properties.getEndpoint().replaceAll("/+$","") + "/" + properties.getBucket() + "/" + key;
        }
    // virtual-hosted-style (bucket.endpoint/key)
    String endpoint = properties.getEndpoint()
        .replace("https://", "")
        .replace("http://", "");
        return "https://" + properties.getBucket() + "." + endpoint + "/" + key;
    }
}
