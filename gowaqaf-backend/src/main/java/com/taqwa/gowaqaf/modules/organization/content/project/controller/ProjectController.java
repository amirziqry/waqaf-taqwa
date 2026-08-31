package com.taqwa.gowaqaf.modules.organization.content.project.controller;

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

import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/project")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping("/create")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectUploadResponse> createProject(@RequestBody ProjectUploadRequest dto) {
		ProjectUploadResponse response = projectService.createProject(dto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectUploadResponse> updateProjectById(@PathVariable UUID id,
			@RequestBody ProjectUploadRequest dto) {
		ProjectUploadResponse response = projectService.updateProjectById(id, dto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{id}/image-keys/upload")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> updateProjectImageKeysById(@PathVariable UUID id,
			@RequestBody List<ProjectImageKey> request) {
		projectService.updateProjectImageKeysById(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<ProjectDetails> getProjectDetailsById(@PathVariable UUID id) {
		ProjectDetails response = projectService.getProjectDetailsById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<ProjectDetails>> getAllProjectsDetails() {
		List<ProjectDetails> response = projectService.getAllProjectsDetails();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> deleteProjectById(@PathVariable UUID id) {
		projectService.deleteProjectById(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
