package com.taqwa.gowaqaf.modules.organization.campaign.component.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.controller.CampaignCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.entity.CampaignCategory;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.mapper.CampaignCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.repository.CampaignCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.service.CampaignCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignCategoryServiceImpl implements CampaignCategoryService {

	private final CampaignCategoryRepository repository;

	@Override
	public CampaignCategoryDto createCategory(CampaignCategoryUploadRequest dto) {
		CampaignCategory category = new CampaignCategory();

		category.setName(dto.name());

		CampaignCategory saved = repository.save(category);

		return CampaignCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public CampaignCategoryDto updateCategory(Long id, CampaignCategoryUploadRequest dto) {
		CampaignCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		category.setName(dto.name());

		CampaignCategory saved = repository.save(category);

		return CampaignCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public CampaignCategory getCategoryById(Long id) {
		CampaignCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		return category;
	}

	@Override
	public CampaignCategoryDto getCategoryDtoById(Long id) {
		CampaignCategory category = getCategoryById(id);

		return CampaignCategoryMapper.mapCategoryToDto(category);
	}

	@Override
	public List<CampaignCategoryDto> getCategoryDtoList() {
		List<CampaignCategory> categories = repository.findAll();

		List<CampaignCategoryDto> dtos = categories.stream().map(c -> CampaignCategoryMapper.mapCategoryToDto(c))
				.toList();

		return dtos;
	}

	@Override
	public void deleteCategory(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id));

		repository.deleteById(id);
	}

}
