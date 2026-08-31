package com.taqwa.gowaqaf.modules.organization.content.component.tag.mapper;

import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;

public class ContentTagMapper {

	public static ContentTagDto mapTagToDto(ContentTag tag) {
		return new ContentTagDto(tag.getId(), tag.getName());
	}

}
