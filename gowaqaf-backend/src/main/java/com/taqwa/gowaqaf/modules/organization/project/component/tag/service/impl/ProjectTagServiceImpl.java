package com.taqwa.gowaqaf.modules.organization.project.component.tag.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.mapper.ProjectTagMapper;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.repository.ProjectTagRepository;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.service.ProjectTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTagServiceImpl implements ProjectTagService {

	private final ProjectTagRepository repository;

	private ProjectTag tryFindTagById(Long id) {
		return repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Tag %s not found.", id)));
	}

	@Override
	public ProjectTagDto createTag(ProjectTagUploadRequest dto) {
		ProjectTag tag = new ProjectTag();

		tag.setName(dto.name());

		ProjectTag saved = repository.save(tag);

		return ProjectTagMapper.mapTagToDto(saved);
	}

	@Override
	public ProjectTagDto updateTag(Long id, ProjectTagUploadRequest dto) {
		ProjectTag tag = tryFindTagById(id);

		tag.setName(dto.name());

		ProjectTag saved = repository.save(tag);

		return ProjectTagMapper.mapTagToDto(saved);
	}

	@Override
	public ProjectTag getTagById(Long id) {
		ProjectTag tag = tryFindTagById(id);

		return tag;
	}

	@Override
	public ProjectTagDto getTagDtoById(Long id) {
		ProjectTag tag = getTagById(id);

		return ProjectTagMapper.mapTagToDto(tag);
	}

	@Override
	public List<ProjectTagDto> getTagDtoList() {
		List<ProjectTag> categories = repository.findAll();

		List<ProjectTagDto> dtos = categories.stream().map(c -> ProjectTagMapper.mapTagToDto(c)).toList();

		return dtos;
	}

	@Override
	public List<ProjectTag> getAllTagById(Set<Long> ids) {
		List<ProjectTag> categories = repository.findAllById(ids);

		if (ids.size() != categories.size())
			throw new ResourceNotFoundException(ErrorCode.PRJ020, "Project Tag(s) not found");

		return categories;
	}

	@Override
	public void deleteTag(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id));

		repository.deleteById(id);
	}

}
