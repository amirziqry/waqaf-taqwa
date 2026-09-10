package com.taqwa.gowaqaf.modules.organization.content.project.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

/**
 * <p>
 * Project management end-point handler
 * <p>
 */
@RestController
@RequestMapping("/api/organization/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	/**
	 * Admin-only to create project.
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectUploadResponse> createProject(@RequestBody ProjectUploadRequest dto) {
		ProjectUploadResponse response = projectService.createProject(dto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Admin-only to update project.
	 * 
	 * @param id
	 * @param dto
	 * @return
	 */
	@PutMapping("/{id}")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectUploadResponse> updateProjectById(@PathVariable UUID id,
			@RequestBody ProjectUploadRequest dto) {
		ProjectUploadResponse response = projectService.updateProjectById(id, dto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Admin-only to update project image keys.
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@PutMapping("/{id}/image-keys")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> updateProjectImageKeysById(@PathVariable UUID id,
			@RequestBody List<ProjectImageKey> request) {
		projectService.updateProjectImageKeysById(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Authenticated-only to get specific project.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ProjectDetails> getProjectDetailsById(@PathVariable UUID id) {
		ProjectDetails response = projectService.getProjectDetailsById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Authenticated-only to get all projects.
	 * 
	 * @param pageable
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Page<ProjectDetails>> getAllProjectsDetails(
			@PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ProjectDetails> response = projectService.getAllProjectsDetails(pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Admin-only to delete project.
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<Void> deleteProjectById(@PathVariable UUID id) {
		projectService.deleteProjectById(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
