package com.taqwa.gowaqaf.modules.organization.project.component.tag.controller;

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

import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.service.ProjectTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/project/tag")
@RequiredArgsConstructor
public class ProjectTagController {

	private final ProjectTagService service;

	@PostMapping("/create")
	public ResponseEntity<ProjectTagDto> createTag(@RequestBody ProjectTagUploadRequest request) {
		ProjectTagDto response = service.createTag(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	public ResponseEntity<ProjectTagDto> updateTag(@PathVariable Long id,
			@RequestBody ProjectTagUploadRequest request) {
		ProjectTagDto response = service.updateTag(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<ProjectTagDto> getTag(@PathVariable Long id) {
		ProjectTagDto response = service.getTagDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<ProjectTagDto>> getTagList() {
		List<ProjectTagDto> response = service.getTagDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
		service.deleteTag(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
