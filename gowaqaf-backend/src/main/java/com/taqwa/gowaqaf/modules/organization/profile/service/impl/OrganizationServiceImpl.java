package com.taqwa.gowaqaf.modules.organization.profile.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUpload;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUploadUrlsResponse;
import com.taqwa.gowaqaf.modules.organization.profile.entity.OrganizationProfile;
import com.taqwa.gowaqaf.modules.organization.profile.mapper.OrganizationMapper;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;
import com.taqwa.gowaqaf.modules.organization.profile.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final StorageService storageService;

	@Override
	public OrganizationProfileUploadUrlsResponse updateProfile(OrganizationProfileUpload dto) {
		OrganizationProfile org = organizationRepository.findFirstBy().orElse(new OrganizationProfile());

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

		OrganizationProfileUploadUrlsResponse uploadUrls = new OrganizationProfileUploadUrlsResponse();

		if (dto.getLogoUploadRequest() != null) {
			dto.getLogoUploadRequest().setPath("organization/images/logo");

			uploadUrls.setLogoUploadUrl(generateImageUploadUrl(dto.getLogoUploadRequest()));
		}

		if (dto.getHeroUploadRequest() != null) {
			dto.getHeroUploadRequest().setPath("organization/images/hero");

			uploadUrls.setHeroUploadUrl(generateImageUploadUrl(dto.getHeroUploadRequest()));
		}

		organizationRepository.save(org);

		return uploadUrls;
	}

	private UploadUrl generateImageUploadUrl(FileUploadRequest request) {
		UploadUrl response = storageService.generateUploadUrl(request);

		return response;
	}

	@Override
	public void uploadImageKeys(OrganizationImagesRequest request) {
		OrganizationProfile org = organizationRepository.findFirstBy().get();
		if (org == null)
			throw new BadRequestException(ErrorCode.A001, "Profile not created");

		String oldLogoKey = org.getLogoKey();
		String oldHeroKey = org.getHeroKey();

		org.setLogoKey(request.getLogoKey());
		org.setHeroKey(request.getHeroKey());

		OrganizationProfile saved = organizationRepository.save(org);

		if (oldLogoKey != null && !oldLogoKey.equals(saved.getLogoKey()))
			storageService.deleteFile(oldLogoKey);

		if (oldHeroKey != null && !oldHeroKey.equals(saved.getHeroKey()))
			storageService.deleteFile(oldHeroKey);

		return;
	}

	@Override
	public OrganizationProfileDetails getProfile() {
		OrganizationProfile org = organizationRepository.findFirstBy().orElse(new OrganizationProfile());

		OrganizationProfileDetails dto = OrganizationMapper.mapToOrganizationProfileDetails(org);

		if (org.getLogoKey() != null)
			dto.setLogoUrl(storageService.generateAccessUrl(org.getLogoKey()));

		if (org.getHeroKey() != null)
			dto.setHeroUrl(storageService.generateAccessUrl(org.getHeroKey()));

		return dto;
	}

}
