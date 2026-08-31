package com.taqwa.gowaqaf.modules.organization.content.news.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.component.image.dto.NewsImageUrl;

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
	private ContentCategoryDto category;
	private Set<ContentTagDto> tags;
	private String summary;
	private String contentHtml;
	private ContentStatus status;
	private List<NewsImageUrl> images;

}
