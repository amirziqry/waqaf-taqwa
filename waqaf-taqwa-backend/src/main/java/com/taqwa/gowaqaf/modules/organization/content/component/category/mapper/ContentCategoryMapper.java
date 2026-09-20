package com.taqwa.gowaqaf.modules.organization.content.component.category.mapper;

import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;

public class ContentCategoryMapper {

	public static ContentCategoryDto mapCategoryToDto(ContentCategory category) {
		return new ContentCategoryDto(category.getId(), category.getName());
	}

}
