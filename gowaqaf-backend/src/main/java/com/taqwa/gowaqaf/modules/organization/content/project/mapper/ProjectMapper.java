package com.taqwa.gowaqaf.modules.organization.content.project.mapper;

import java.util.HashSet;
import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.content.component.category.mapper.ContentCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.mapper.ContentTagMapper;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;

public class ProjectMapper {

	public static ProjectDetails mapToProjectDetails(Project entity) {

		ProjectDetails dto = new ProjectDetails();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSlugUrl(entity.getSlugUrl());
		dto.setTargetAmount(entity.getTargetAmount());
		dto.setLocation(entity.getLocation());
		dto.setDate(entity.getDate());

		if (entity.getCategory() == null)
			dto.setCategory(null);
		else
			dto.setCategory(ContentCategoryMapper.mapCategoryToDto(entity.getCategory()));

		if (entity.getTags().isEmpty())
			dto.setTags(new HashSet<>());
		else
			dto.setTags(entity.getTags().stream().map(tag -> ContentTagMapper.mapTagToDto(tag))
					.collect(Collectors.toSet()));

		dto.setSummary(entity.getSummary());
		dto.setContentHtml(entity.getContentHtml());
		dto.setStatus(entity.getStatus());
		dto.setImages(null);

		return dto;
	}

}
