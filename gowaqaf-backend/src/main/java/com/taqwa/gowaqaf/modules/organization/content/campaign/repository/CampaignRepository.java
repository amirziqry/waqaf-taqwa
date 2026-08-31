package com.taqwa.gowaqaf.modules.organization.content.campaign.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.content.campaign.entity.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

}
