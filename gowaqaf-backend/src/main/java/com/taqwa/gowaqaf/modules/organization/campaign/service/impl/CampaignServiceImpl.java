package com.taqwa.gowaqaf.modules.organization.campaign.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.entity.CampaignCategory;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.repository.CampaignCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImage;
import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.repository.CampaignTagRepository;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.campaign.entity.Campaign;
import com.taqwa.gowaqaf.modules.organization.campaign.mapper.CampaignMapper;
import com.taqwa.gowaqaf.modules.organization.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.campaign.service.CampaignService;
import com.taqwa.gowaqaf.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

	private final CampaignRepository campaignRepository;
	private final CampaignCategoryRepository campaignCategoryRepository;
	private final CampaignTagRepository campaignTagRepository;
	private final StorageService storageService;

	@Override
	public CampaignUploadResponse createCampaign(CampaignUploadRequest dto) {
		Campaign campaign = new Campaign();

		campaign.setName(dto.getName());
		campaign.setSlugUrl(dto.getSlugUrl());
		campaign.setDateStart(dto.getDateStart());
		campaign.setDateEnd(dto.getDateEnd());

		CampaignCategory category = campaignCategoryRepository.findById(dto.getCategory().getId()).get();
		campaign.setCategory(category);

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<CampaignTag> tags = new HashSet<>(campaignTagRepository.findAllById(tagIds));
			campaign.setTags(tags);
		}

		campaign.setSummary(dto.getSummary());
		campaign.setContentHtml(dto.getContentHtml());
		campaign.setStatus(dto.getStatus());
		campaign.setImages(new ArrayList<>());

		Campaign saved = campaignRepository.save(campaign);

		List<UploadUrl> uploadUrls = null;
		if (dto.getImageUploadRequests() != null)
			uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new CampaignUploadResponse(saved.getId(), uploadUrls);
	}

	@Override
	public CampaignUploadResponse updateCampaignById(UUID id, CampaignUploadRequest dto) {
		return null;
	}

	private List<UploadUrl> generateImageUploadUrls(UUID id, List<FileUploadRequest> files) {
		files.forEach(file -> file.setPath(String.format("campaign/%s/images/", id.toString())));

		List<UploadUrl> response = storageService.generateUploadUrls(files);

		return response;
	}

	@Override
	public void updateCampaignImageKeysById(UUID id, List<CampaignImageKey> request) {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CPG001, String.format("Campaign %s not found", id)));

		List<CampaignImage> existingImages = campaign.getImages();

		// IDs that are still present in the frontend's new image stack
		Set<Long> requestedIds = request.stream().map(CampaignImageKey::getId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// Remove images that no longer exist in the new stack
		existingImages.removeIf(image -> {

			if (!requestedIds.contains(image.getId())) {
				storageService.deleteFile(image.getImageKey());
				return true;
			}

			return false;
		});

		// Add new images
		List<CampaignImage> campaignImages = request.stream().filter(image -> image.getId() == null)
				.map(imageRequest -> {

					CampaignImage image = new CampaignImage();

					image.setImageKey(imageRequest.getKey());
					image.setCampaign(campaign);

					return image;
				}).toList();

		existingImages.addAll(campaignImages);

		campaignRepository.save(campaign);
	}

	@Override
	public CampaignDetails getCampaignDetailsById(UUID id) {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CPG001, String.format("Campaign %s not found", id)));

		CampaignDetails dto = CampaignMapper.mapToCampaignDetails(campaign);

		if (campaign.getImages() != null) {
			dto.setImages(campaign.getImages().stream().map(image -> {
				CampaignImageUrl imageUrl = new CampaignImageUrl();

				imageUrl.setId(image.getId());
				imageUrl.setUrl(storageService.generateAccessUrl(image.getImageKey()));

				return imageUrl;
			}).toList());
		}

		return dto;
	}

	@Override
	public List<CampaignDetails> getAllCampaigns() {
		List<Campaign> campaigns = campaignRepository.findAll();
		if (campaigns.size() == 0)
			throw new BadRequestException(ErrorCode.CPG001, String.format("Campaign not found"));

		List<CampaignDetails> dtos = campaigns.stream().map(n -> {
			CampaignDetails dto = CampaignMapper.mapToCampaignDetails(n);

			dto.setImages(n.getImages().stream().map(image -> {
				CampaignImageUrl imageUrl = new CampaignImageUrl();

				imageUrl.setId(image.getId());
				imageUrl.setUrl(storageService.generateAccessUrl(image.getImageKey()));

				return imageUrl;
			}).toList());

			return dto;
		}).toList();

		return dtos;
	}

	@Override
	public void deleteCampaignById(UUID id) {
		if (!campaignRepository.existsById(id))
			throw new ResourceNotFoundException(ErrorCode.CPG001, String.format("Campaign %s not found", id));

		campaignRepository.deleteById(id);

		return;
	}

}
