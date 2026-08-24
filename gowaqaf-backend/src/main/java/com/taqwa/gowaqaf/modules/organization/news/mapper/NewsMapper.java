package com.taqwa.gowaqaf.modules.organization.news.mapper;

import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryDto;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagMapper;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.news.entity.News;

public class NewsMapper {

	public static NewsDetails mapToNewsDetails(News news) {

		NewsDetails dto = new NewsDetails();

		dto.setId(news.getId());
		dto.setTitle(news.getTitle());
		dto.setSlugUrl(news.getSlugUrl());
		dto.setAuthor(news.getAuthor());
		dto.setDate(news.getDate());
		dto.setCategory(new NewsCategoryDto(news.getCategory().getId(), news.getCategory().getName()));
		dto.setTags(news.getTags().stream().map(tag -> NewsTagMapper.mapTagToDto(tag)).collect(Collectors.toSet()));
		dto.setSummary(news.getSummary());
		dto.setContentHtml(news.getContentHtml());
		dto.setStatus(news.getStatus());
		dto.setImages(null);

		return dto;
	}

}
