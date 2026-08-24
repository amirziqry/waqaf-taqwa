package com.taqwa.gowaqaf.storage.client.s3.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Value("${storage.endpoint}")
	private String endpoint;

	@Value("${storage.region}")
	private String region;

	@Value("${storage.access-key}")
	private String accessKey;

	@Value("${storage.secret-key}")
	private String secretKey;

	@Bean
	S3Client s3Client() {
		return S3Client.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).build();
	}

	@Bean
	S3Presigner s3Presigner() {
		return S3Presigner.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).build();
	}

}
