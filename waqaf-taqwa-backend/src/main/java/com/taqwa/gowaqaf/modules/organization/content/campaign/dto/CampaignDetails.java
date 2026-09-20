package com.taqwa.gowaqaf.modules.organization.content.campaign.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.content.component.category.dto.ContentCategoryDto;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.dto.ContentTagDto;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDetails {

	private UUID id;
	private String name;
	private String slugUrl;
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateStart;
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateEnd;
	private ContentCategoryDto category;
	private Set<ContentTagDto> tags;
	private String summary;
	private String contentHtml;
	private ContentStatus status;
	private List<CampaignImageUrl> images;

}
