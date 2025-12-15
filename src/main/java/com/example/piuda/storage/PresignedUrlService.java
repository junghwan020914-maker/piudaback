package com.example.piuda.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스토리지 URL을 Presigned URL로 변환하는 공통 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PresignedUrlService {

    private final StorageService storageService;

    /**
     * 단일 URL을 presigned URL로 변환
     * @param storedUrl 데이터베이스에 저장된 전체 URL
     * @return presigned URL (60분 유효)
     */
    public String convertToPresignedUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isEmpty()) {
            return "";
        }
        
        try {
            String key = extractKey(storedUrl);
            log.debug("Converting URL to presigned - Original: {}, Key: {}", storedUrl, key);
            return storageService.getPresignedUrl(key, 60);
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for: {}", storedUrl, e);
            return storedUrl; // 실패 시 원본 URL 반환
        }
    }

    /**
     * 여러 URL을 presigned URL로 변환
     * @param storedUrls 데이터베이스에 저장된 URL 목록
     * @return presigned URL 목록
     */
    public List<String> convertToPresignedUrls(List<String> storedUrls) {
        if (storedUrls == null) {
            return List.of();
        }
        
        return storedUrls.stream()
                .map(this::convertToPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * 저장된 URL에서 오브젝트 key 추출
     * @param storedUrl 전체 URL
     * @return 오브젝트 key
     */
    private String extractKey(String storedUrl) {
        if (storedUrl == null || storedUrl.isEmpty()) {
            return "";
        }
        
        // URL에서 key 추출
        // 예: https://kr1-object-storage.kr1.cloud.toast.com/v1/XdjLAhCjInhjzgvd/piuda-storage/report/20250101120000_abc123.jpg
        // -> report/20250101120000_abc123.jpg
        String key = storedUrl;
        
        // publicBaseUrl 패턴으로 저장된 경우
        if (storedUrl.contains("/piuda-storage/")) {
            key = storedUrl.substring(storedUrl.indexOf("/piuda-storage/") + "/piuda-storage/".length());
        } 
        // bucket 이름 이후 부분 추출
        else if (storedUrl.contains("piuda-storage")) {
            int bucketIndex = storedUrl.indexOf("piuda-storage");
            if (bucketIndex != -1) {
                String afterBucket = storedUrl.substring(bucketIndex + "piuda-storage".length());
                if (afterBucket.startsWith("/")) {
                    key = afterBucket.substring(1);
                }
            }
        }
        
        return key;
    }
}
