package com.taqwa.gowaqaf.modules.organization.project.component.category.controller;

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

import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.category.service.ProjectCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/project/category")
@RequiredArgsConstructor
public class ProjectCategoryController {

	private final ProjectCategoryService service;

	@PostMapping("/create")
	public ResponseEntity<ProjectCategoryDto> createCategory(@RequestBody ProjectCategoryUploadRequest request) {
		ProjectCategoryDto response = service.createCategory(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update/")
	public ResponseEntity<ProjectCategoryDto> updateCategory(@PathVariable Long id,
			@RequestBody ProjectCategoryUploadRequest request) {
		ProjectCategoryDto response = service.updateCategory(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<ProjectCategoryDto> getCategory(@PathVariable Long id) {
		ProjectCategoryDto response = service.getCategoryDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<ProjectCategoryDto>> getCategoryList() {
		List<ProjectCategoryDto> response = service.getCategoryDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		service.deleteCategory(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
