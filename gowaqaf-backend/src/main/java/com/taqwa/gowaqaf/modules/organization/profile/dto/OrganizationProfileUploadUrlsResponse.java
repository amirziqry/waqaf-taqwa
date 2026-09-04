package com.taqwa.gowaqaf.modules.organization.profile.dto;

import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationProfileUploadUrlsResponse {
	
	UploadUrl logoUploadUrl;
	
	UploadUrl heroUploadUrl;

}
