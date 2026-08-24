package com.taqwa.gowaqaf.modules.organization.campaign.component.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;

public interface CampaignTagRepository extends JpaRepository<CampaignTag, Long> {

}
