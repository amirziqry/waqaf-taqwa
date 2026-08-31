package com.taqwa.gowaqaf.modules.organization.content.campaign.service;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadResponse;

public interface CampaignService {

	CampaignUploadResponse createCampaign(CampaignUploadRequest dto);

	CampaignUploadResponse updateCampaignById(UUID id, CampaignUploadRequest dto);

	void updateCampaignImageKeysById(UUID id, List<CampaignImageKey> request);

	CampaignDetails getCampaignDetailsById(UUID id);

	List<CampaignDetails> getAllCampaigns();

	void deleteCampaignById(UUID id);

}
