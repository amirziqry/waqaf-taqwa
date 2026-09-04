package com.taqwa.gowaqaf.modules.organization.profile.dto;

import java.util.UUID;

import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationProfileUpload {

	private UUID id;
	private String name;
	private String phone;
	private String email;
	private Address address;
	private String contentHtml;
	private FileUploadRequest logoUploadRequest;
	private FileUploadRequest heroUploadRequest;

}
