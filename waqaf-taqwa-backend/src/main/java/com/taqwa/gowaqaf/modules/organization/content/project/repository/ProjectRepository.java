package com.taqwa.gowaqaf.modules.organization.content.project.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	List<Project> findAllBy(Pageable pageable);

	@Modifying
	@Query("""
			   UPDATE Project p
			   SET p.collectedAmount = p.collectedAmount + :amount
			   WHERE p.id = :id
			""")
	int incrementCollectedAmountById(@Param("id") UUID id, @Param("amount") BigDecimal amount);

}
