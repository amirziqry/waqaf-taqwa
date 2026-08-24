package com.taqwa.gowaqaf.modules.organization.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.project.component.category.dto.ProjectCategoryDto;
import com.taqwa.gowaqaf.modules.organization.project.component.image.dto.ProjectImageUrl;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.dto.ProjectTagDto;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetails {

	private UUID id;
	private String name;
	private String slugUrl;
	private BigDecimal collectedAmount;
	private BigDecimal targetAmount;
	private String location;
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate date;
	private ProjectCategoryDto category;
	private Set<ProjectTagDto> tags;
	private String summary;
	private String contentHtml;
	private Status status;
	private List<ProjectImageUrl> images;

}
