package com.taqwa.gowaqaf.modules.organization.news.component.category.controller;

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

import com.taqwa.gowaqaf.modules.organization.news.component.category.NewsCategoryDto;
import com.taqwa.gowaqaf.modules.organization.news.component.category.service.NewsCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/news/category")
@RequiredArgsConstructor
public class NewsCategoryController {

	private final NewsCategoryService service;

	@PostMapping("/create")
	public ResponseEntity<NewsCategoryDto> createCategory(@RequestBody NewsCategoryUploadRequest request) {
		NewsCategoryDto response = service.createCategory(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update/")
	public ResponseEntity<NewsCategoryDto> updateCategory(@PathVariable Long id,
			@RequestBody NewsCategoryUploadRequest request) {
		NewsCategoryDto response = service.updateCategory(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<NewsCategoryDto> getCategory(@PathVariable Long id) {
		NewsCategoryDto response = service.getCategoryDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<NewsCategoryDto>> getCategoryList() {
		List<NewsCategoryDto> response = service.getCategoryDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		service.deleteCategory(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
