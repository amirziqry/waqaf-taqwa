package com.taqwa.gowaqaf.modules.organization.news.component.tag.service;

import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTag;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagDto;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.dto.NewsTagUploadRequest;

public interface NewsTagService {

	NewsTagDto createTag(NewsTagUploadRequest dto);

	NewsTagDto updateTag(Long id, NewsTagUploadRequest dto);

	NewsTag getTagById(Long id);

	NewsTagDto getTagDtoById(Long id);

	List<NewsTagDto> getTagDtoList();

	List<NewsTag> getAllTagById(Set<Long> ids);

	void deleteTag(Long id);

}
