package com.taqwa.gowaqaf.modules.organization.content.component.tag.controller;

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

import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.service.ContentTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class ContentTagController {

	private final ContentTagService service;

	@PostMapping("/{type}/tag/create")
	public ResponseEntity<ContentTagDto> createTag(@PathVariable String type,
			@RequestBody ContentTagUploadRequest request) {
		ContentTagDto response = service.createTag(ContentType.valueOf(type.toUpperCase()), request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{type}/tag/{id}/update")
	public ResponseEntity<ContentTagDto> updateTag(@PathVariable String type, @PathVariable Long id,
			@RequestBody ContentTagUploadRequest request) {
		ContentTagDto response = service.updateTag(ContentType.valueOf(type.toUpperCase()), id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{type}/tag/{id}/get")
	public ResponseEntity<ContentTagDto> getTag(@PathVariable String type, @PathVariable Long id) {
		ContentTagDto response = service.getTagDtoById(ContentType.valueOf(type.toUpperCase()), id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{type}/tag/all/get")
	public ResponseEntity<List<ContentTagDto>> getTagList(@PathVariable String type) {
		List<ContentTagDto> response = service.getAllTagDto(ContentType.valueOf(type.toUpperCase()));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{type}/tag/{id}/delete")
	public ResponseEntity<Void> deleteTag(@PathVariable String type, @PathVariable Long id) {
		service.deleteTag(ContentType.valueOf(type.toUpperCase()), id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
