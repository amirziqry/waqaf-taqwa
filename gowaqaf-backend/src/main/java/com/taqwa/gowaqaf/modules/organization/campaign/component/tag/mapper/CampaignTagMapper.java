package com.taqwa.gowaqaf.modules.organization.campaign.component.tag.mapper;

import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;

public class CampaignTagMapper {

	public static CampaignTagDto mapTagToDto(CampaignTag tag) {
		return new CampaignTagDto(tag.getId(), tag.getName());
	}

	public static CampaignTag mapDtoToCampaign(CampaignTagDto dto) {
		return new CampaignTag(dto.getId(), dto.getName());
	}

}
