package com.taqwa.gowaqaf.modules.organization.project.component.tag.service;

import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;

public interface ProjectTagService {

	ProjectTag getTagById(Long id);

	ProjectTagDto getTagDtoById(Long id);

	List<ProjectTagDto> getTagDtoList();

	List<ProjectTag> getAllTagById(Set<Long> ids);

	ProjectTagDto createTag(ProjectTagUploadRequest dto);

	void deleteTag(Long id);

	ProjectTagDto updateTag(Long id, ProjectTagUploadRequest dto);

}
