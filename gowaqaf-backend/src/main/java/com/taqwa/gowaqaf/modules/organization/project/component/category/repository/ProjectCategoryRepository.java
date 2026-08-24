package com.taqwa.gowaqaf.modules.organization.project.component.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

}
