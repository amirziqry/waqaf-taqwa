package com.taqwa.gowaqaf.modules.organization.project.component.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {

}
