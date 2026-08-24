package com.taqwa.gowaqaf.modules.organization.campaign.mapper;

import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.mapper.CampaignTagMapper;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.campaign.entity.Campaign;

public class CampaignMapper {

	public static CampaignDetails mapToCampaignDetails(Campaign campaign) {
		CampaignDetails dto = new CampaignDetails();

		dto.setId(campaign.getId());
		dto.setName(campaign.getName());
		dto.setSlugUrl(campaign.getSlugUrl());
		dto.setDateStart(campaign.getDateStart());
		dto.setDateEnd(campaign.getDateEnd());
		dto.setCategory(new CampaignCategoryDto(campaign.getCategory().getId(), campaign.getCategory().getName()));
		dto.setTags(campaign.getTags().stream().map(tag -> CampaignTagMapper.mapTagToDto(tag))
				.collect(Collectors.toSet()));
		dto.setSummary(campaign.getSummary());
		dto.setContentHtml(campaign.getContentHtml());
		dto.setStatus(campaign.getStatus());
		dto.setImages(null);

		return dto;
	}

}
