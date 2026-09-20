package com.taqwa.gowaqaf.modules.organization.about.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUpload;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUploadUrlsResponse;
import com.taqwa.gowaqaf.modules.organization.about.entity.OrganizationAbout;
import com.taqwa.gowaqaf.modules.organization.about.mapper.OrganizationMapper;
import com.taqwa.gowaqaf.modules.organization.about.repository.OrganizationAboutRepository;
import com.taqwa.gowaqaf.modules.organization.about.service.OrganizationAboutService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationAboutService {

	private final OrganizationAboutRepository repository;
	private final StorageService storageService;

	@Override
	public OrganizationAboutUploadUrlsResponse updateAbout(OrganizationAboutUpload dto) {
		OrganizationAbout org = repository.findFirstBy().orElse(new OrganizationAbout());

		org.setName(dto.getName());
		org.setPhone(dto.getPhone());
		org.setEmail(dto.getEmail());
		org.setAddressLine1(dto.getAddress().getAddressLine1());
		org.setAddressLine2(dto.getAddress().getAddressLine2());
		org.setAddressLine3(dto.getAddress().getAddressLine3());
		org.setPostcode(dto.getAddress().getPostcode());
		org.setCity(dto.getAddress().getCity());
		org.setState(dto.getAddress().getState());
		org.setCountry(dto.getAddress().getCountry());
		org.setContentHtml(dto.getContentHtml());

		OrganizationAboutUploadUrlsResponse uploadUrls = new OrganizationAboutUploadUrlsResponse();

		if (dto.getLogoUploadRequest() != null) {
			dto.getLogoUploadRequest().setPath("organization/images/logo");

			uploadUrls.setLogoUploadUrl(generateImageUploadUrl(dto.getLogoUploadRequest()));
		}

		if (dto.getHeroUploadRequest() != null) {
			dto.getHeroUploadRequest().setPath("organization/images/hero");

			uploadUrls.setHeroUploadUrl(generateImageUploadUrl(dto.getHeroUploadRequest()));
		}

		repository.save(org);

		return uploadUrls;
	}

	private UploadUrl generateImageUploadUrl(FileUploadRequest request) {
		UploadUrl response = storageService.generateUploadUrl(request);

		return response;
	}

	@Override
	public void uploadImageKeys(OrganizationImagesRequest request) {
		OrganizationAbout org = repository.findFirstBy().get();
		if (org == null)
			throw new BadRequestException(ErrorCode.A001, "Profile not created");

		String oldLogoKey = org.getLogoKey();
		String oldHeroKey = org.getHeroKey();

		org.setLogoKey(request.getLogoKey());
		org.setHeroKey(request.getHeroKey());

		OrganizationAbout saved = repository.save(org);

		if (oldLogoKey != null && !oldLogoKey.equals(saved.getLogoKey()))
			storageService.deleteFile(oldLogoKey);

		if (oldHeroKey != null && !oldHeroKey.equals(saved.getHeroKey()))
			storageService.deleteFile(oldHeroKey);

		return;
	}

	@Override
	@Transactional(readOnly = true)
	public OrgAboutDetails getAbout() {
		OrganizationAbout org = repository.findFirstBy().orElse(new OrganizationAbout());

		OrgAboutDetails dto = OrganizationMapper.mapToOrganizationProfileDetails(org);

		if (org.getLogoKey() != null)
			dto.setLogoUrl(storageService.generateAccessUrl(org.getLogoKey()));

		if (org.getHeroKey() != null)
			dto.setHeroUrl(storageService.generateAccessUrl(org.getHeroKey()));

		return dto;
	}

}
