package com.taqwa.gowaqaf.modules.organization.project.component.category.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;

public interface ProjectCategoryService {

	ProjectCategoryDto createCategory(ProjectCategoryUploadRequest dto);

	ProjectCategoryDto updateCategory(Long id, ProjectCategoryUploadRequest dto);

	ProjectCategory getCategoryById(Long id);

	ProjectCategoryDto getCategoryDtoById(Long id);

	List<ProjectCategoryDto> getCategoryDtoList();

	void deleteCategory(Long id);

}
