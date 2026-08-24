package com.taqwa.gowaqaf.modules.organization.project.component.category.mapper;

import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;

public class ProjectCategoryMapper {

	public static ProjectCategoryDto mapCategoryToDto(ProjectCategory category) {
		return new ProjectCategoryDto(category.getId(), category.getName());
	}

	public static ProjectCategory mapDtoToCategory(ProjectCategoryDto dto) {
		return new ProjectCategory(dto.getId(), dto.getName());
	}

}
