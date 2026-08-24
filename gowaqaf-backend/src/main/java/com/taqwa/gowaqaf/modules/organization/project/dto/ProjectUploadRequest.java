package com.taqwa.gowaqaf.modules.organization.project.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;
import com.taqwa.gowaqaf.storage.dto.FileUploadRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUploadRequest {

	private String name;
	private String slugUrl;
	private BigDecimal collectedAmount;
	private BigDecimal targetAmount;
	private String location;
	private ProjectCategoryDto category;
	private Set<ProjectTagDto> tags;
	private String summary;
	private String contentHtml;
	private Status status;
	private List<FileUploadRequest> imageUploadRequests;

}
