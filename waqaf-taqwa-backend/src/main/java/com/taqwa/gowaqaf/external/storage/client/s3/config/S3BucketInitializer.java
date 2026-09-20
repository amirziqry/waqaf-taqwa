package com.taqwa.gowaqaf.external.storage.client.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Component
@RequiredArgsConstructor
public class S3BucketInitializer {

	private final S3Client s3Client;

	@Value("${storage.bucket}")
	private String bucket;

	@PostConstruct
	public void initializeBucket() {
		try {
			s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());

		} catch (NoSuchBucketException e) {
			try {
				s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

			} catch (Exception ex) {
				System.out.println("Could not create S3 bucket: " + ex.getMessage());
			}

		} catch (Exception ex) {
			System.out.println("S3 unavailable. Skipping bucket initialization.");
		}
	}
}
