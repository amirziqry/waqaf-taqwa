package com.taqwa.gowaqaf.modules.organization.news.component.tag;

public class NewsTagMapper {

	public static NewsTagDto mapTagToDto(NewsTag tag) {
		return new NewsTagDto(tag.getId(), tag.getName());
	}

	public static NewsTag mapDtoToTag(NewsTagDto dto) {
		return new NewsTag(dto.getId(), dto.getName());
	}

}