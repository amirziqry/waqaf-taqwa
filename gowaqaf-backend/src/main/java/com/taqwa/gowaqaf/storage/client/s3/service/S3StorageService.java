package com.taqwa.gowaqaf.storage.client.s3.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${storage.bucket}")
	private String bucket;

	@Override
	public UploadUrl generateUploadUrl(FileUploadRequest request) {
		String imageKey = request.getPath() + "/" + generateUniqueFileName(request.getFilename());

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(imageKey)
				.contentType(request.getContentType()).build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(5)).putObjectRequest(putObjectRequest).build();

		String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

		return new UploadUrl(uploadUrl, imageKey);
	}

	@Override
	public List<UploadUrl> generateUploadUrls(List<FileUploadRequest> request) {

		List<UploadUrl> uploads = request.stream().map(file -> {

			String imageKey = file.getPath() + "/" + generateUniqueFileName(file.getFilename());

			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(imageKey)
					.contentType(file.getContentType()).build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(10)).putObjectRequest(putObjectRequest).build();

			String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

			return new UploadUrl(uploadUrl, imageKey);

		}).toList();

		return uploads;
	}

	private String generateUniqueFileName(String filename) {
		String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

		return uniqueId + "-" + filename;
	}

	@Override
	public String generateAccessUrl(String imageKey) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(imageKey).build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(10)).getObjectRequest(getObjectRequest).build();

		return s3Presigner.presignGetObject(presignRequest).url().toString();
	}

	@Override
	public void deleteFile(String imageKey) {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(imageKey).build());
	}

}
