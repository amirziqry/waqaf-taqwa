package com.taqwa.gowaqaf.modules.organization.content.component.category.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;

public interface ContentCategoryService {

	ContentCategoryDto createCategory(ContentType type, ContentCategoryUploadRequest dto);

	ContentCategoryDto updateCategory(ContentType type, Long id, ContentCategoryUploadRequest dto);

	ContentCategory getCategoryById(ContentType type, Long id);

	ContentCategoryDto getCategoryDtoById(ContentType type, Long id);

	List<ContentCategoryDto> getCategoryDtoList(ContentType type);

	void deleteCategory(ContentType type, Long id);

}
