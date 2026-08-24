package com.taqwa.gowaqaf.storage.service;

import java.util.List;

import com.taqwa.gowaqaf.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.storage.dto.UploadUrl;

public interface StorageService {
	
	UploadUrl generateUploadUrl(FileUploadRequest request);

	List<UploadUrl> generateUploadUrls(List<FileUploadRequest> request);

	String generateAccessUrl(String imageKey);

	void deleteFile(String imageKey);

}
