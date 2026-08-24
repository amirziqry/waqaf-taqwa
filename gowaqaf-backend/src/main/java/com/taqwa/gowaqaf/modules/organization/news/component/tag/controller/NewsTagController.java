package com.taqwa.gowaqaf.modules.organization.news.component.tag.controller;

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

import com.taqwa.gowaqaf.modules.organization.news.component.tag.NewsTagDto;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.dto.NewsTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.news.component.tag.service.NewsTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/news/tag")
@RequiredArgsConstructor
public class NewsTagController {

	private final NewsTagService service;

	@PostMapping("/create")
	public ResponseEntity<NewsTagDto> createTag(@RequestBody NewsTagUploadRequest request) {
		NewsTagDto response = service.createTag(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	public ResponseEntity<NewsTagDto> updateTag(@PathVariable Long id,
			@RequestBody NewsTagUploadRequest request) {
		NewsTagDto response = service.updateTag(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<NewsTagDto> getTag(@PathVariable Long id) {
		NewsTagDto response = service.getTagDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<NewsTagDto>> getTagList() {
		List<NewsTagDto> response = service.getTagDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
		service.deleteTag(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}

