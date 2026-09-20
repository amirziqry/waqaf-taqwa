package com.taqwa.gowaqaf.modules.organization.about.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.about.entity.OrganizationAbout;

public interface OrganizationAboutRepository extends JpaRepository<OrganizationAbout, UUID> {

	Optional<OrganizationAbout> findFirstBy();

}
