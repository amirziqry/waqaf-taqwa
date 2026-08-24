package com.taqwa.gowaqaf.modules.organization.campaign.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImageUrl;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagDto;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;

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
	private CampaignCategoryDto category;
	private Set<CampaignTagDto> tags;
	private String summary;
	private String contentHtml;
	private Status status;
	private List<CampaignImageUrl> images;

}
