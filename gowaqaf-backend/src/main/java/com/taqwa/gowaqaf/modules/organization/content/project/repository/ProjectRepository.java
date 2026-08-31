package com.taqwa.gowaqaf.modules.organization.content.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectWithCollection;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	Project findByName(String name);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectWithCollection(
			    p,
			    COALESCE(SUM(d.amount), 0)
			)
			FROM Project p
			LEFT JOIN PersonalDonation d
			    ON d.project.id = p.id
			    AND d.status = 'PAID'
			WHERE p.id = :id
			GROUP BY p
			""")
	Optional<ProjectWithCollection> findWithCollectionById(@Param("id") UUID id);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectWithCollection(
			    p,
			    COALESCE(SUM(d.amount), 0)
			)
			FROM Project p
			LEFT JOIN PersonalDonation d
			    ON d.project.id = p.id
			    AND d.status = 'PAID'
			GROUP BY p
			""")
	List<ProjectWithCollection> findAllWithCollection();
}
