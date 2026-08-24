package com.taqwa.gowaqaf.modules.organization.news.component.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategory;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryDto;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.news.component.category.controller.NewsCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.news.component.category.service.NewsCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsCategoryServiceImpl implements NewsCategoryService {

	private final NewsCategoryRepository repository;

	@Override
	public NewsCategoryDto createCategory(NewsCategoryUploadRequest dto) {
		NewsCategory category = new NewsCategory();

		category.setName(dto.name());

		NewsCategory saved = repository.save(category);

		return NewsCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public NewsCategoryDto updateCategory(Long id, NewsCategoryUploadRequest dto) {
		NewsCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		category.setName(dto.name());

		NewsCategory saved = repository.save(category);

		return NewsCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public NewsCategory getCategoryById(Long id) {
		NewsCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		return category;
	}

	@Override
	public NewsCategoryDto getCategoryDtoById(Long id) {
		NewsCategory category = getCategoryById(id);

		return NewsCategoryMapper.mapCategoryToDto(category);
	}

	@Override
	public List<NewsCategoryDto> getCategoryDtoList() {
		List<NewsCategory> categories = repository.findAll();

		List<NewsCategoryDto> dtos = categories.stream().map(c -> NewsCategoryMapper.mapCategoryToDto(c))
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