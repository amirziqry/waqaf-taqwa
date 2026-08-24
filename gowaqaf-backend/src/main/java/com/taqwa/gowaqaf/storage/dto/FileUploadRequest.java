package com.taqwa.gowaqaf.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

	private String filename;

	private String contentType;

	private String path;

}
