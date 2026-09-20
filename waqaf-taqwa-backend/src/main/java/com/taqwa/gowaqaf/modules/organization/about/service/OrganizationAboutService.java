package com.taqwa.gowaqaf.modules.organization.about.service;

import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUpload;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUploadUrlsResponse;

public interface OrganizationAboutService {

	OrganizationAboutUploadUrlsResponse updateAbout(OrganizationAboutUpload dto);

	void uploadImageKeys(OrganizationImagesRequest request);

	OrgAboutDetails getAbout();

}
