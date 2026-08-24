package com.taqwa.gowaqaf.module.storage;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class StorageTest {

	private final S3Client s3Client;

	@Value("${storage.bucket}")
	private String bucket;

	@BeforeAll
	void setupBucket() {
		try {
			s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
		} catch (NoSuchBucketException e) {
			s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
		}
	}

	@Test
	void shouldConnectToLocalStack() {

		ListBucketsResponse response = s3Client.listBuckets();

		assertNotNull(response);
	}

	@Test
	void shouldUploadImage() {

		s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

		String key = "test/test-image.jpg";

		s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType("image/jpeg").build(),
				RequestBody.fromFile(Paths.get("src/test/resources/dump1.jpg")));
	}

	@Disabled
	@AfterEach
	void cleanup() {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key("test/test-image.jpg").build());
	}

}
