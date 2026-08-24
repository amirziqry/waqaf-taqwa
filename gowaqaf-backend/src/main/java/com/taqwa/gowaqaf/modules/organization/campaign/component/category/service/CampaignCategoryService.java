package com.taqwa.gowaqaf.modules.organization.campaign.component.category.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.organization.campaign.component.category.controller.CampaignCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.entity.CampaignCategory;

public interface CampaignCategoryService {

	CampaignCategoryDto createCategory(CampaignCategoryUploadRequest dto);

	CampaignCategoryDto updateCategory(Long id, CampaignCategoryUploadRequest dto);

	CampaignCategory getCategoryById(Long id);

	CampaignCategoryDto getCategoryDtoById(Long id);

	List<CampaignCategoryDto> getCategoryDtoList();

	void deleteCategory(Long id);

}
