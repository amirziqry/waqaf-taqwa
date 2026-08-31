package com.taqwa.gowaqaf.modules.organization.content.component.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.category.service.ContentCategoryService;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class ContentCategoryController {

	private final ContentCategoryService service;

	@PostMapping("/{type}/category/create") // project / news / campaign
	public ResponseEntity<ContentCategoryDto> createCategory(@PathVariable String type,
			@RequestBody ContentCategoryUploadRequest request) {
		ContentCategoryDto response = service.createCategory(ContentType.valueOf(type.toUpperCase()), request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{type}/category/{id}/update/")
	public ResponseEntity<ContentCategoryDto> updateCategory(@PathVariable String type, @PathVariable Long id,
			@RequestBody ContentCategoryUploadRequest request) {
		ContentCategoryDto response = service.updateCategory(ContentType.valueOf(type.toUpperCase()), id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{type}/category/{id}/get")
	public ResponseEntity<ContentCategoryDto> getCategory(@PathVariable String type, @PathVariable Long id) {
		ContentCategoryDto response = service.getCategoryDtoById(ContentType.valueOf(type.toUpperCase()), id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{type}/category/all/get")
	public ResponseEntity<List<ContentCategoryDto>> getCategoryList(@PathVariable String type) {
		List<ContentCategoryDto> response = service.getCategoryDtoList(ContentType.valueOf(type.toUpperCase()));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{type}/category/{id}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable String type, @PathVariable Long id) {
		service.deleteCategory(ContentType.valueOf(type.toUpperCase()), id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
