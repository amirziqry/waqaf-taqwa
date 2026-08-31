package com.taqwa.gowaqaf.modules.organization.content.news.mapper;

import java.util.HashSet;
import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.content.component.category.mapper.ContentCategoryMapper;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.mapper.ContentTagMapper;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;

public class NewsMapper {

	public static NewsDetails mapToNewsDetails(News news) {

		NewsDetails dto = new NewsDetails();

		dto.setId(news.getId());
		dto.setTitle(news.getTitle());
		dto.setSlugUrl(news.getSlugUrl());
		dto.setAuthor(news.getAuthor());
		dto.setDate(news.getDate());

		if (news.getCategory() == null)
			dto.setCategory(null);
		else
			dto.setCategory(ContentCategoryMapper.mapCategoryToDto(news.getCategory()));

		if (news.getTags().isEmpty())
			dto.setTags(new HashSet<>());
		else
			dto.setTags(
					news.getTags().stream().map(tag -> ContentTagMapper.mapTagToDto(tag)).collect(Collectors.toSet()));

		dto.setSummary(news.getSummary());
		dto.setContentHtml(news.getContentHtml());
		dto.setStatus(news.getStatus());
		dto.setImages(null);

		return dto;
	}

}
