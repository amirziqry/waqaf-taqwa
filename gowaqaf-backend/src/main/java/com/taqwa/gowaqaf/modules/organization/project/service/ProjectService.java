package com.taqwa.gowaqaf.modules.organization.project.service;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectUploadRequest;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.project.entity.Project;

public interface ProjectService {

	ProjectUploadResponse createProject(ProjectUploadRequest dto);

	ProjectUploadResponse updateProjectById(UUID id, ProjectUploadRequest dto);

	void updateProjectImageKeysById(UUID id, List<ProjectImageKey> request);

	ProjectDetails getProjectDetailsById(UUID id);

	List<ProjectDetails> getAllProjectsDetails();

	void deleteProjectById(UUID id);

	Project getProjectById(UUID id);

}
