package com.taqwa.gowaqaf.modules.donation.project.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;

public interface ProjectDonationRepository extends JpaRepository<ProjectDonation, UUID> {

	Optional<ProjectDonation> findByIdAndPersonalId(UUID donationId, UUID personalId);

	Optional<ProjectDonation> findByTransaction_WebhookToken(String token);

	Optional<ProjectDonation> findByTransaction_BillingCode(String billingCode);

	Optional<ProjectDonation> findByPersonal_IdAndTransaction_Id(UUID personalId, UUID donationId);

	Optional<ProjectDonation> findByPersonal_IdAndTransaction_BillingCode(UUID personalId, String billingCode);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM ProjectDonation pd
			JOIN pd.transaction d
			WHERE pd.project.id = :projectId
			  AND d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("projectId") UUID projectId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
