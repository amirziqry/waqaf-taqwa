package com.taqwa.gowaqaf.modules.organization.content.component.tag.service;

import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;

public interface ContentTagService {

	ContentTagDto createTag(ContentType type, ContentTagUploadRequest dto);

	ContentTagDto updateTag(ContentType type, Long id, ContentTagUploadRequest dto);

	ContentTag getTagById(ContentType type, Long id);

	ContentTagDto getTagDtoById(ContentType type, Long id);

	List<ContentTagDto> getAllTagDto(ContentType type);

	List<ContentTag> getAllTagById(ContentType type, Set<Long> ids);

	void deleteTag(ContentType type, Long id);

}
