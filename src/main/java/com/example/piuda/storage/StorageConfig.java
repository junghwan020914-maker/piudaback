package com.example.piuda.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties props) {
    return S3Client.builder()
        .httpClientBuilder(ApacheHttpClient.builder())
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
        ))
        .endpointOverride(URI.create(props.getEndpoint()))
        .region(Region.of(props.getRegion()))
        .forcePathStyle(props.isPathStyle())
        .build();
    }

    @Bean
    public StorageService storageService(S3Client client, StorageProperties props) {
        return new S3StorageService(client, props);
    }

    // No fallback: always use S3StorageService
}
