package com.taqwa.gowaqaf.modules.organization.content.project.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.external.storage.service.StorageService;
import com.taqwa.gowaqaf.modules.organization.content.component.category.service.ContentCategoryService;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.service.ContentTagService;
import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.project.component.image.entity.ProjectImage;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectWithCollection;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.mapper.ProjectMapper;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ContentCategoryService categoryService;
	private final ContentTagService tagService;
	private final StorageService storageService;

	@Override
	public ProjectUploadResponse createProject(ProjectUploadRequest dto) {
		Project project = new Project();

		project.setName(dto.getName());
		project.setSlugUrl(dto.getSlugUrl());
		project.setTargetAmount(dto.getTargetAmount());
		project.setLocation(dto.getLocation());
		project.setDate(LocalDate.now());

		if (dto.getCategory() != null)
			project.setCategory(categoryService.getCategoryById(ContentType.PROJECT, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.PROJECT, tagIds));

			project.setTags(tags);
		}

		project.setSummary(dto.getSummary());
		project.setContentHtml(dto.getContentHtml());
		project.setStatus(dto.getStatus());
		project.setImages(new ArrayList<>());

		Project saved = projectRepository.save(project);

		List<UploadUrl> uploadUrls = null;
		if (dto.getImageUploadRequests() != null)
			uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new ProjectUploadResponse(saved.getId(), uploadUrls);
	}

	@Override
	public ProjectUploadResponse updateProjectById(UUID id, ProjectUploadRequest dto) {
		Project project = getProjectById(id);

		project.setName(dto.getName());
		project.setSlugUrl(dto.getSlugUrl());
		project.setTargetAmount(dto.getTargetAmount());
		project.setLocation(dto.getLocation());

		if (dto.getCategory() != null)
			project.setCategory(categoryService.getCategoryById(ContentType.PROJECT, dto.getCategory().getId()));

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ContentTag> tags = new HashSet<>(tagService.getAllTagById(ContentType.PROJECT, tagIds));

			project.setTags(tags);
		}

		project.setSummary(dto.getSummary());
		project.setContentHtml(dto.getContentHtml());
		project.setStatus(dto.getStatus());

		Project saved = projectRepository.save(project);

		List<UploadUrl> uploadUrls = null;
		if (dto.getImageUploadRequests() != null)
			uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new ProjectUploadResponse(saved.getId(), uploadUrls);
	}

	private List<UploadUrl> generateImageUploadUrls(UUID id, List<FileUploadRequest> files) {
		files.forEach(file -> file.setPath(String.format("project/%s/images/", id.toString())));

		List<UploadUrl> response = storageService.generateUploadUrls(files);

		return response;
	}

	@Override
	public void updateProjectImageKeysById(UUID id, List<ProjectImageKey> request) {
		Project project = getProjectById(id);

		List<ProjectImage> existingImages = project.getImages();

		// IDs that are still present in the frontend's new image stack
		Set<Long> requestedIds = request.stream().map(ProjectImageKey::getId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// Remove images that no longer exist in the new stack
		existingImages.removeIf(image -> {

			if (!requestedIds.contains(image.getId())) {
				storageService.deleteFile(image.getImageKey());
				return true;
			}

			return false;
		});

		// Add new images
		List<ProjectImage> newImages = request.stream().filter(image -> image.getId() == null).map(imageRequest -> {

			ProjectImage image = new ProjectImage();

			image.setImageKey(imageRequest.getImageKey());
			image.setProject(project);

			return image;
		}).toList();

		existingImages.addAll(newImages);

		projectRepository.save(project);
	}

	@Override
	public Project getProjectById(UUID id) {
		Project project = projectRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

		return project;
	}

	@Override
	public ProjectDetails getProjectDetailsById(UUID id) {
		ProjectWithCollection projectWithCollection = projectRepository.findWithCollectionById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

		ProjectDetails dto = mapToProjectDetails(projectWithCollection);

		return dto;
	}

	@Override
	public List<ProjectDetails> getAllProjectsDetails() {
		List<ProjectWithCollection> projectsWithCollection = projectRepository.findAllWithCollection();
		if (projectsWithCollection.isEmpty())
			return List.of();

		List<ProjectDetails> dtos = projectsWithCollection.stream().map(project -> mapToProjectDetails(project))
				.toList();

		return dtos;
	}

	private ProjectDetails mapToProjectDetails(ProjectWithCollection projectWithCollection) {
		Project project = projectWithCollection.getProject();

		ProjectDetails dto = ProjectMapper.mapToProjectDetails(project);

		dto.setCollectedAmount(projectWithCollection.getCollectedAmount());

		dto.setImages(project.getImages().stream().map(image -> {

			ProjectImageUrl imageUrl = new ProjectImageUrl();
			imageUrl.setId(image.getId());
			imageUrl.setUrl(storageService.generateAccessUrl(image.getImageKey()));

			return imageUrl;

		}).toList());

		return dto;
	}

	@Override
	public void deleteProjectById(UUID id) {
		Project project = projectRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

		// Delete image files from object storage
		for (ProjectImage image : project.getImages()) {
			storageService.deleteFile(image.getImageKey());
		}

		// Delete project and its ProjectImage records
		projectRepository.delete(project);
	}

}
