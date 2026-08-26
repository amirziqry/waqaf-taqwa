package com.taqwa.gowaqaf.modules.organization.news.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.news.component.image.dto.NewsImageKey;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsUploadRequest;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsUploadResponse;
import com.taqwa.gowaqaf.modules.organization.news.service.NewsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/news")
@RequiredArgsConstructor
public class NewsController {

	private final NewsService newsService;

	@PostMapping("/create")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<NewsUploadResponse> createNews(@RequestBody NewsUploadRequest request) {
		NewsUploadResponse response = newsService.createNews(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<NewsUploadResponse> updateNewsById(@PathVariable UUID id,
			@RequestBody NewsUploadRequest request) {
		NewsUploadResponse response = newsService.updateNewsById(id, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/images/upload")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> updateNewsImageKeysById(@PathVariable UUID id, @RequestBody List<NewsImageKey> request) {
		newsService.uploadNewsImageKeysById(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<NewsDetails> getNewsDetailsById(@PathVariable UUID id) {
		NewsDetails response = newsService.getNewsDetailsById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<NewsDetails>> getAllNewsDetails() {
		List<NewsDetails> response = newsService.getAllNews();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> deleteNewsById(@PathVariable UUID id) {
		newsService.deleteNewsById(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
