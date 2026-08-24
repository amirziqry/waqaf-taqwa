package com.taqwa.gowaqaf.modules.organization.campaign.component.tag.service;

import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;

public interface CampaignTagService {

	CampaignTagDto createTag(CampaignTagUploadRequest dto);

	CampaignTagDto updateTag(Long id, CampaignTagUploadRequest dto);

	CampaignTag getTagById(Long id);

	CampaignTagDto getTagDtoById(Long id);

	List<CampaignTagDto> getTagDtoList();

	List<CampaignTag> getAllTagById(Set<Long> ids);

	void deleteTag(Long id);

}
