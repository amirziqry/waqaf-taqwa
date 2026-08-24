package com.taqwa.gowaqaf.modules.organization.news.component.category;

public class NewsCategoryMapper {

	public static NewsCategoryDto mapCategoryToDto(NewsCategory category) {
		return new NewsCategoryDto(category.getId(), category.getName());
	}

	public static NewsCategory mapDtoToCategory(NewsCategory dto) {
		return new NewsCategory(dto.getId(), dto.getName());
	}

}
