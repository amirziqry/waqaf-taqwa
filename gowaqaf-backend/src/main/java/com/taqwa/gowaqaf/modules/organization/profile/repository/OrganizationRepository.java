package com.taqwa.gowaqaf.modules.organization.profile.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.profile.entity.OrganizationProfile;

public interface OrganizationRepository extends JpaRepository<OrganizationProfile, UUID> {

	Optional<OrganizationProfile> findFirstBy();

}
