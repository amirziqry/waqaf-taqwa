package com.taqwa.gowaqaf.modules.donation.personal.project.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface ProjectDonationAdminRepository extends JpaRepository<PersonalDonation, UUID> {

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation pd
			JOIN pd.transaction d
			WHERE pd.project.id = :projectId
			  AND d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("projectId") UUID projectId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
