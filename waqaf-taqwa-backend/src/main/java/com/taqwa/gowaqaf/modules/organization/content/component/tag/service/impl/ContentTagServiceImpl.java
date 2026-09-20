package com.taqwa.gowaqaf.modules.organization.content.component.tag.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.mapper.ContentTagMapper;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.repository.ContentTagRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.service.ContentTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentTagServiceImpl implements ContentTagService {

	private final ContentTagRepository repository;

	@Override
	public ContentTagDto createTag(ContentType type, ContentTagUploadRequest dto) {
		ContentTag tag = new ContentTag();

		tag.setName(dto.name());
		tag.setType(type);

		ContentTag saved = repository.save(tag);

		return ContentTagMapper.mapTagToDto(saved);
	}

	@Override
	public ContentTagDto updateTag(ContentType type, Long id, ContentTagUploadRequest dto) {
		ContentTag tag = getTagById(type, id);

		tag.setName(dto.name());

		ContentTag saved = repository.save(tag);

		return ContentTagMapper.mapTagToDto(saved);
	}

	@Override
	public ContentTag getTagById(ContentType type, Long id) {
		ContentTag tag = repository.findByIdAndType(id, type).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.TAG001, String.format("Tag %s not found.", id)));

		return tag;
	}

	@Override
	public ContentTagDto getTagDtoById(ContentType type, Long id) {
		ContentTag tag = getTagById(type, id);

		return ContentTagMapper.mapTagToDto(tag);
	}

	@Override
	public List<ContentTagDto> getAllTagDto(ContentType type) {
		List<ContentTag> categories = repository.findAllByType(type);

		List<ContentTagDto> dtos = categories.stream().map(c -> ContentTagMapper.mapTagToDto(c)).toList();

		return dtos;
	}

	@Override
	public List<ContentTag> getAllTagById(ContentType type, Set<Long> ids) {
		List<ContentTag> categories = repository.findAllByIdInAndType(ids, type);

		if (ids.size() != categories.size())
			throw new ResourceNotFoundException(ErrorCode.TAG001, "Tag(s) not found");

		return categories;
	}

	@Override
	public void deleteTag(ContentType type, Long id) {
		if (!repository.existsByIdAndType(id, type))
			throw new ResourceNotFoundException(ErrorCode.TAG001, String.format("Tag %s not found.", id));

		repository.deleteById(id);
	}

}
