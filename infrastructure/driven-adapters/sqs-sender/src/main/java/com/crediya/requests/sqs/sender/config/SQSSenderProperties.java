package com.crediya.requests.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SQSSenderProperties(
     String region,
     String endpoint,
     String accessKeyId,
     String secretAccessKey,
     Map<String, String> queueUrls
){ }
