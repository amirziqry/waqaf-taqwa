package com.taqwa.gowaqaf.modules.organization.project.service.impl;

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
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;
import com.taqwa.gowaqaf.modules.organization.project.component.category.service.ProjectCategoryService;
import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageUrl;
import com.taqwa.gowaqaf.modules.organization.project.component.image.entity.ProjectImage;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.service.ProjectTagService;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.project.mapper.ProjectMapper;
import com.taqwa.gowaqaf.modules.organization.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.project.service.ProjectService;
import com.taqwa.gowaqaf.payment.dto.CollectionCreateRequest;
import com.taqwa.gowaqaf.payment.dto.CollectionStatus;
import com.taqwa.gowaqaf.payment.service.PaymentService;
import com.taqwa.gowaqaf.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.storage.dto.UploadUrl;
import com.taqwa.gowaqaf.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectCategoryService categoryService;
	private final ProjectTagService tagService;
	private final StorageService storageService;
	private final PaymentService paymentService;

	@Override
	public ProjectUploadResponse createProject(ProjectUploadRequest dto) {
		Project project = new Project();

		project.setName(dto.getName());
		project.setSlugUrl(dto.getSlugUrl());
		project.setCollectedAmount(dto.getCollectedAmount());
		project.setTargetAmount(dto.getTargetAmount());
		project.setLocation(dto.getLocation());
		project.setDate(LocalDate.now());

		if (dto.getCategory() != null) {
			ProjectCategory category = categoryService.getCategoryById(dto.getCategory().getId());
			project.setCategory(category);
		}

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ProjectTag> tags = new HashSet<>(tagService.getAllTagById(tagIds));

			project.setTags(tags);
		}

		project.setSummary(dto.getSummary());
		project.setContentHtml(dto.getContentHtml());
		project.setStatus(dto.getStatus());
		project.setImages(new ArrayList<>());
		project.setPaymentCollectionCode(generatePaymentCollectionCode(dto));

		Project saved = projectRepository.save(project);

		List<UploadUrl> uploadUrls = null;
		if (dto.getImageUploadRequests() != null)
			uploadUrls = generateImageUploadUrls(saved.getId(), dto.getImageUploadRequests());

		return new ProjectUploadResponse(saved.getId(), uploadUrls);
	}

	private String generatePaymentCollectionCode(ProjectUploadRequest dto) {
		CollectionCreateRequest request = new CollectionCreateRequest();

		request.setName(dto.getName());
		request.setDescription(dto.getName() + "Payment Gateway Collection Record.");
		request.setStatus(CollectionStatus.ACTIVE);

		String collectionCode = paymentService.createPaymentCollection(request);

		if (collectionCode == null)
			throw new BadRequestException(ErrorCode.COL001, "Fail to create project collection.");

		return collectionCode;
	}

	@Override
	public ProjectUploadResponse updateProjectById(UUID id, ProjectUploadRequest dto) {
		Project project = projectRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

		project.setName(dto.getName());
		project.setSlugUrl(dto.getSlugUrl());
		project.setCollectedAmount(dto.getCollectedAmount());
		project.setTargetAmount(dto.getTargetAmount());
		project.setLocation(dto.getLocation());

		if (dto.getCategory() != null) {
			ProjectCategory category = categoryService.getCategoryById(dto.getCategory().getId());
			project.setCategory(category);
		}

		if (dto.getTags() != null) {
			Set<Long> tagIds = dto.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet());
			Set<ProjectTag> tags = new HashSet<>(tagService.getAllTagById(tagIds));

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
		Project project = projectRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

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
	public ProjectDetails getProjectDetailsById(UUID id) {
		Project project = projectRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id)));

		ProjectDetails dto = ProjectMapper.mapToProjectDetails(project);

		dto.setImages(project.getImages().stream().map(image -> {
			ProjectImageUrl imageUrl = new ProjectImageUrl();

			imageUrl.setId(image.getId());
			imageUrl.setUrl(storageService.generateAccessUrl(image.getImageKey()));

			return imageUrl;
		}).toList());

		return dto;
	}

	@Override
	public List<ProjectDetails> getAllProjectsDetails() {
		List<Project> projects = projectRepository.findAll();
		if (projects.size() == 0)
			throw new BadRequestException(ErrorCode.A001, "No news found.");

		List<ProjectDetails> dtos = projects.stream().map(project -> {

			ProjectDetails dto = ProjectMapper.mapToProjectDetails(project);

			dto.setImages(project.getImages().stream().map(image -> {
				ProjectImageUrl imageUrl = new ProjectImageUrl();

				imageUrl.setId(image.getId());
				imageUrl.setUrl(storageService.generateAccessUrl(image.getImageKey()));

				return imageUrl;
			}).toList());

			return dto;
		}).toList();

		return dtos;
	}

	@Override
	public void deleteProjectById(UUID id) {
		if (!projectRepository.existsById(id))
			throw new ResourceNotFoundException(ErrorCode.PRJ001, String.format("Project %s not found", id));

		projectRepository.deleteById(id);

		return;
	}

}
