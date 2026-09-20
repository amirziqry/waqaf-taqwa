package com.taqwa.gowaqaf.external.storage.service;

import java.util.List;

import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;

public interface StorageService {
	
	UploadUrl generateUploadUrl(FileUploadRequest request);

	List<UploadUrl> generateUploadUrls(List<FileUploadRequest> request);

	String generateAccessUrl(String imageKey);

	void deleteFile(String imageKey);

}
