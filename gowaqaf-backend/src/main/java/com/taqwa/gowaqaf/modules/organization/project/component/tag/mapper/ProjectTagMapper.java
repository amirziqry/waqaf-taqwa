package com.taqwa.gowaqaf.modules.organization.project.component.tag.mapper;

import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;

public class ProjectTagMapper {

	public static ProjectTagDto mapTagToDto(ProjectTag tag) {
		return new ProjectTagDto(tag.getId(), tag.getName());
	}

	public static ProjectTag mapDtoToTag(ProjectTagDto dto) {
		return new ProjectTag(dto.getId(), dto.getName());
	}

}
