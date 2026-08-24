package com.taqwa.gowaqaf.modules.organization.project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	Project findByName(String name);

}
