package com.taqwa.gowaqaf.modules.organization.content.campaign.dto;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.external.storage.dto.UploadUrl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUploadResponse {

	private UUID id;
	private List<UploadUrl> uploadUrls;

}
