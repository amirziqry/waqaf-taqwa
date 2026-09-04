package com.taqwa.gowaqaf.module.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class StorageServiceTest {

	private final StorageService storageService;
	private final S3Client s3Client;

	@Value("${storage.bucket}")
	private String bucket;

	@Test
	void shouldUploadImageUsingPresignedUrl() throws Exception {

		Path imagePath = Paths.get("src/test/resources/dump1.jpg");

		FileUploadRequest file = new FileUploadRequest();
		file.setFilename("test-image.jpg");
		file.setContentType("image/jpeg");
		file.setPath("test");

		List<UploadUrl> response = storageService.generateUploadUrls(List.of(file));

		UploadUrl upload = response.get(0);

		String uploadUrl = upload.getUploadUrl();
		String imageKey = upload.getImageKey();

		System.out.println("Upload URL: " + uploadUrl);
		System.out.println("Image key: " + imageKey);

		// Act as the frontend and upload the image
		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(uploadUrl))
				.header("Content-Type", "image/jpeg").PUT(HttpRequest.BodyPublishers.ofFile(imagePath)).build();

		HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, httpResponse.statusCode());

		// Verify the object actually exists in LocalStack
		HeadObjectResponse head = s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(imageKey).build());

		assertEquals(Files.size(imagePath), head.contentLength());

		// Cleanup
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(imageKey).build());
	}

	@Test
	void shouldGenerateAccessUrl() throws Exception {

		Path imagePath = Paths.get("src/test/resources/dump1.jpg");

		// Create an object for this test
		String imageKey = "test/access-test-image.jpg";

		s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(imageKey).contentType("image/jpeg").build(),
				imagePath);

		try {
			// Generate the access URL
			String accessUrl = storageService.generateAccessUrl(imageKey);

			assertNotNull(accessUrl);

			// Act as the frontend/browser
			HttpClient httpClient = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(accessUrl)).GET().build();

			HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

			assertEquals(200, response.statusCode());

			// Verify we actually received the image
			byte[] expected = Files.readAllBytes(imagePath);

			assertArrayEquals(expected, response.body());

		} finally {
			s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(imageKey).build());
		}
	}

	@Test
	void shouldDeleteFile() throws Exception {

		Path imagePath = Paths.get("src/test/resources/dump1.jpg");

		String imageKey = "test/delete-test-image.jpg";

		s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(imageKey).contentType("image/jpeg").build(),
				imagePath);

		// Make sure the object exists before testing deletion
		assertDoesNotThrow(() -> s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(imageKey).build()));

		// Test your actual service
		storageService.deleteFile(imageKey);

		// Object should no longer exist
		assertThrows(NoSuchKeyException.class,
				() -> s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(imageKey).build()));
	}

}
