package com.taqwa.gowaqaf.modules.organization.news.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTag;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagDto;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagMapper;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagRepository;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.dto.NewsTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.service.NewsTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsTagServiceImpl implements NewsTagService {

	private final NewsTagRepository repository;

	private NewsTag tryFindTagById(Long id) {
		return repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Tag %s not found.", id)));
	}

	@Override
	public NewsTagDto createTag(NewsTagUploadRequest dto) {
		NewsTag tag = new NewsTag();

		tag.setName(dto.name());

		NewsTag saved = repository.save(tag);

		return NewsTagMapper.mapTagToDto(saved);
	}

	@Override
	public NewsTagDto updateTag(Long id, NewsTagUploadRequest dto) {
		NewsTag tag = tryFindTagById(id);

		tag.setName(dto.name());

		NewsTag saved = repository.save(tag);

		return NewsTagMapper.mapTagToDto(saved);
	}

	@Override
	public NewsTag getTagById(Long id) {
		NewsTag tag = tryFindTagById(id);

		return tag;
	}

	@Override
	public NewsTagDto getTagDtoById(Long id) {
		NewsTag tag = getTagById(id);

		return NewsTagMapper.mapTagToDto(tag);
	}

	@Override
	public List<NewsTagDto> getTagDtoList() {
		List<NewsTag> categories = repository.findAll();

		List<NewsTagDto> dtos = categories.stream().map(c -> NewsTagMapper.mapTagToDto(c)).toList();

		return dtos;
	}

	@Override
	public List<NewsTag> getAllTagById(Set<Long> ids) {
		List<NewsTag> categories = repository.findAllById(ids);

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
