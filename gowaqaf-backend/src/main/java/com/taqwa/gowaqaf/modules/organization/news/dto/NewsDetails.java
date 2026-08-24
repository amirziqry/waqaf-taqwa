package com.taqwa.gowaqaf.modules.organization.news.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryDto;
import com.taqwa.gowaqaf.modules.organization.news.component.image.dto.NewsImageUrl;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagDto;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDetails {

	private UUID id;
	private String title;
	private String slugUrl;
	private String author;
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate date;
	private NewsCategoryDto category;
	private Set<NewsTagDto> tags;
	private String summary;
	private String contentHtml;
	private Status status;
	private List<NewsImageUrl> images;

}
