package com.taqwa.gowaqaf.modules.organization.content.project.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.organization.content.project.component.image.dto.ProjectImageKey;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;

public interface ProjectService {

	ProjectUploadResponse createProject(ProjectUploadRequest dto);

	ProjectUploadResponse updateProjectById(UUID id, ProjectUploadRequest dto);

	void updateProjectImageKeysById(UUID id, List<ProjectImageKey> request);

	Project getProjectById(UUID id);

	ProjectDetails getProjectDetailsById(UUID id);

	List<ProjectDetails> getProjectsDetailsList(Pageable pageable);

	Page<ProjectDetails> getAllProjectsDetails(Pageable pageable);

	void deleteProjectById(UUID id);

	void updateProjectCollectedAmountById(UUID id, BigDecimal amount);

}
