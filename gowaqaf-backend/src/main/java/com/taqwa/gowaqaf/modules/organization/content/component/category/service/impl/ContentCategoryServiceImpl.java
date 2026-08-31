package com.taqwa.gowaqaf.modules.organization.content.component.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.category.mapper.ContentCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.content.component.category.repository.ContentCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.category.service.ContentCategoryService;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentCategoryServiceImpl implements ContentCategoryService {

	private final ContentCategoryRepository repository;

	@Override
	public ContentCategoryDto createCategory(ContentType type, ContentCategoryUploadRequest dto) {
		ContentCategory category = new ContentCategory();

		category.setName(dto.name());
		category.setType(type);

		ContentCategory saved = repository.save(category);

		return ContentCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public ContentCategoryDto updateCategory(ContentType type, Long id, ContentCategoryUploadRequest dto) {
		ContentCategory category = getCategoryById(type, id);

		category.setName(dto.name());

		ContentCategory saved = repository.save(category);

		return ContentCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public ContentCategory getCategoryById(ContentType type, Long id) {
		ContentCategory category = repository.findByIdAndType(id, type).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		return category;
	}

	@Override
	public ContentCategoryDto getCategoryDtoById(ContentType type, Long id) {
		ContentCategory category = getCategoryById(type, id);

		return ContentCategoryMapper.mapCategoryToDto(category);
	}

	@Override
	public List<ContentCategoryDto> getCategoryDtoList(ContentType type) {
		List<ContentCategory> categories = repository.findAllByType(type);

		List<ContentCategoryDto> dtos = categories.stream().map(c -> ContentCategoryMapper.mapCategoryToDto(c))
				.toList();

		return dtos;
	}

	@Override
	public void deleteCategory(ContentType type, Long id) {
		if (!repository.existsByIdAndType(type, id))
			throw new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id));

		repository.deleteById(id);
	}

}
