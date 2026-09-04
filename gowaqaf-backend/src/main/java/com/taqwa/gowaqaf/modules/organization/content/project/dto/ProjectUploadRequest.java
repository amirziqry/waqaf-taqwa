package com.taqwa.gowaqaf.modules.organization.content.project.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.taqwa.gowaqaf.external.storage.dto.FileUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUploadRequest {

	private String name;
	private String slugUrl;
	private BigDecimal targetAmount;
	private String location;
	private ContentCategoryDto category;
	private Set<ContentTagDto> tags;
	private String summary;
	private String contentHtml;
	private ContentStatus status;
	private List<FileUploadRequest> imageUploadRequests;

}
