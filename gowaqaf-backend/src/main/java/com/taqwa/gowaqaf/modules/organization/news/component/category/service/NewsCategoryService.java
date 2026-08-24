package com.taqwa.gowaqaf.modules.organization.news.component.category.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategory;
import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryDto;
import com.taqwa.gowaqaf.modules.organization.news.component.category.controller.NewsCategoryUploadRequest;

public interface NewsCategoryService {

	NewsCategoryDto createCategory(NewsCategoryUploadRequest dto);

	NewsCategoryDto updateCategory(Long id, NewsCategoryUploadRequest dto);

	NewsCategory getCategoryById(Long id);

	NewsCategoryDto getCategoryDtoById(Long id);

	List<NewsCategoryDto> getCategoryDtoList();

	void deleteCategory(Long id);

}
