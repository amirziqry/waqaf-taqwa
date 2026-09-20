package com.taqwa.gowaqaf.modules.organization.content.campaign.mapper;

import java.util.HashSet;
import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.entity.Campaign;
import com.taqwa.gowaqaf.modules.organization.content.component.category.mapper.ContentCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.mapper.ContentTagMapper;

public class CampaignMapper {

	public static CampaignDetails mapToCampaignDetails(Campaign campaign) {
		CampaignDetails dto = new CampaignDetails();

		dto.setId(campaign.getId());
		dto.setName(campaign.getName());
		dto.setSlugUrl(campaign.getSlugUrl());
		dto.setDateStart(campaign.getDateStart());
		dto.setDateEnd(campaign.getDateEnd());

		if (campaign.getCategory() == null)
			dto.setCategory(null);
		else
			dto.setCategory(ContentCategoryMapper.mapCategoryToDto(campaign.getCategory()));

		if (campaign.getTags().isEmpty())
			dto.setTags(new HashSet<>());
		else
			dto.setTags(campaign.getTags().stream().map(tag -> ContentTagMapper.mapTagToDto(tag))
					.collect(Collectors.toSet()));

		dto.setSummary(campaign.getSummary());
		dto.setContentHtml(campaign.getContentHtml());
		dto.setStatus(campaign.getStatus());
		dto.setImages(null);

		return dto;
	}

}
