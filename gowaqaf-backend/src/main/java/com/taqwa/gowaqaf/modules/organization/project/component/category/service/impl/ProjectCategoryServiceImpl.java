package com.taqwa.gowaqaf.modules.organization.project.component.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;
import com.taqwa.gowaqaf.modules.organization.project.component.category.mapper.ProjectCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.project.component.category.repository.ProjectCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.project.component.category.service.ProjectCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectCategoryServiceImpl implements ProjectCategoryService {

	private final ProjectCategoryRepository repository;

	@Override
	public ProjectCategoryDto createCategory(ProjectCategoryUploadRequest dto) {
		ProjectCategory category = new ProjectCategory();

		category.setName(dto.name());

		ProjectCategory saved = repository.save(category);

		return ProjectCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public ProjectCategoryDto updateCategory(Long id, ProjectCategoryUploadRequest dto) {
		ProjectCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		category.setName(dto.name());

		ProjectCategory saved = repository.save(category);

		return ProjectCategoryMapper.mapCategoryToDto(saved);
	}

	@Override
	public ProjectCategory getCategoryById(Long id) {
		ProjectCategory category = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id)));

		return category;
	}

	@Override
	public ProjectCategoryDto getCategoryDtoById(Long id) {
		ProjectCategory category = getCategoryById(id);

		return ProjectCategoryMapper.mapCategoryToDto(category);
	}

	@Override
	public List<ProjectCategoryDto> getCategoryDtoList() {
		List<ProjectCategory> categories = repository.findAll();

		List<ProjectCategoryDto> dtos = categories.stream().map(c -> ProjectCategoryMapper.mapCategoryToDto(c))
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
