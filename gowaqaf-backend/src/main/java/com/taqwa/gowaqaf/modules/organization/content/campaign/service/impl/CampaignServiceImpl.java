package com.taqwa.gowaqaf.modules.organization.content.campaign.service.impl;

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
import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.entity.CampaignImage;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.campaign.entity.Campaign;
import com.taqwa.gowaqaf.modules.organization.content.campaign.mapper.CampaignMapper;
import com.taqwa.gowaqaf.modules.organization.content.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.content.campaign.service.CampaignService;
import com.taqwa.gowaqaf.modules.organization.content.component.category.service.ContentCategoryService;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.service.ContentTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

	private final CampaignRepository campaignRepository;
	private final ContentCategoryService categoryService;
	private final ContentTagService tagService;
	private final StorageService storageService;

	@Override
	public CampaignUploadResponse createCampaign(CampaignUploadRequest dto) {
		Campaign campaign = new Campaign();

		campaign.setName(dto.getName());
		campaign.setSlugUrl(dto.getSlugUrl());
		campaign.setDateStart(dto.getDateStart());
		campaign.setDateEnd(dto.getDateEnd());

		if (dto.getCategory() != null)
			campaign.setCategory(categoryService.getCategoryById(ContentType.CAMPAIGN, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.CAMPAIGN, tagIds));

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
		Campaign campaign = campaignRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CPG001, String.format("Campaign %s not found", id)));

		campaign.setName(dto.getName());
		campaign.setSlugUrl(dto.getSlugUrl());
		campaign.setDateStart(dto.getDateStart());
		campaign.setDateEnd(dto.getDateEnd());

		if (dto.getCategory() != null)
			campaign.setCategory(categoryService.getCategoryById(ContentType.CAMPAIGN, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.CAMPAIGN, tagIds));

			campaign.setTags(tags);
		}

		campaign.setSummary(dto.getSummary());
		campaign.setContentHtml(dto.getContentHtml());
		campaign.setStatus(dto.getStatus());

		Campaign saved = campaignRepository.save(campaign);

		List<UploadUrl> uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new CampaignUploadResponse(saved.getId(), uploadUrls);
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
		Campaign campaign = campaignRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CPG001, String.format("Campaign %s not found", id)));

		// Delete image files from object storage
		for (CampaignImage image : campaign.getImages()) {
			storageService.deleteFile(image.getImageKey());
		}

		// Delete project and its ProjectImage records
		campaignRepository.delete(campaign);
	}

}
