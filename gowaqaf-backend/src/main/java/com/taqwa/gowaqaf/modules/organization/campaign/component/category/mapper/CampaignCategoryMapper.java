package com.taqwa.gowaqaf.modules.organization.campaign.component.category.mapper;

import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.entity.CampaignCategory;

public class CampaignCategoryMapper {

	public static CampaignCategoryDto mapCategoryToDto(CampaignCategory category) {
		return new CampaignCategoryDto(category.getId(), category.getName());
	}

	public static CampaignCategory mapDtoToCategory(CampaignCategoryDto dto) {
		return new CampaignCategory(dto.getId(), dto.getName());
	}

}
