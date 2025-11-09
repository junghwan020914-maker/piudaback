package com.example.piuda.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nhn.storage")
public class StorageProperties {
    // removed: always enabled
    /**
     * S3-compatible endpoint, e.g. https://kr.object.nhncloud.com
     */
    private String endpoint;
    /**
     * AWS region-style string, e.g. kr-standard
     */
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;
    /**
     * If true, build path-style URLs: endpoint/bucket/key
     */
    private boolean pathStyle = true;
    /**
     * Optional public base URL (e.g., CDN). If set, returned URLs use this as base.
     */
    private String publicBaseUrl;
    /**
     * Whether to set objects as public-read on upload.
     */
    private boolean publicRead = true;

    /**
     * Optional key prefix to namespace all objects (e.g., "dev/" or "prod/").
     */
    private String keyPrefix;
}
