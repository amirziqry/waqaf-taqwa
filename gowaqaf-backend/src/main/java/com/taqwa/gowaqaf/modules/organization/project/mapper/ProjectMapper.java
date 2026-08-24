package com.taqwa.gowaqaf.modules.organization.project.mapper;

import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.project.component.category.mapper.ProjectCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.mapper.ProjectTagMapper;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.project.entity.Project;

public class ProjectMapper {

	public static ProjectDetails mapToProjectDetails(Project entity) {

		ProjectDetails dto = new ProjectDetails();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSlugUrl(entity.getSlugUrl());
		dto.setCollectedAmount(entity.getCollectedAmount());
		dto.setTargetAmount(entity.getTargetAmount());
		dto.setLocation(entity.getLocation());
		dto.setDate(entity.getDate());
		dto.setCategory(ProjectCategoryMapper.mapCategoryToDto(entity.getCategory()));
		dto.setTags(
				entity.getTags().stream().map(tag -> ProjectTagMapper.mapTagToDto(tag)).collect(Collectors.toSet()));
		dto.setSummary(entity.getSummary());
		dto.setContentHtml(entity.getContentHtml());
		dto.setStatus(entity.getStatus());
		dto.setImages(null);

		return dto;
	}

}
