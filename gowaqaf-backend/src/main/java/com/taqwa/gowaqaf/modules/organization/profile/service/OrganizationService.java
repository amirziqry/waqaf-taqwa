package com.taqwa.gowaqaf.modules.organization.profile.service;

import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUpload;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUploadUrlsResponse;

public interface OrganizationService {

	OrganizationProfileUploadUrlsResponse updateProfile(OrganizationProfileUpload dto);

	void uploadImageKeys(OrganizationImagesRequest request);

	OrganizationProfileDetails getProfile();

}
