package com.taqwa.gowaqaf.modules.organization.collection.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

	Optional<Donation> findByWebhookToken(String token);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo(
			    COALESCE(SUM(CASE
			        WHEN d.donationType = 'DIRECT'
			        THEN d.amount ELSE 0
			    END), 0),

			    COALESCE(SUM(CASE
			        WHEN d.donationType = 'RECURRING'
			        THEN d.amount ELSE 0
			    END), 0),

			    COALESCE(SUM(CASE
			        WHEN d.donationType = 'PROJECT'
			        THEN d.amount ELSE 0
			    END), 0),

			    COALESCE(SUM(CASE
			        WHEN d.donationType = 'MERCHANT'
			        THEN d.amount ELSE 0
			    END), 0),

			    COALESCE(SUM(CASE
			        WHEN d.donationType = 'RAKANQR'
			        THEN d.amount ELSE 0
			    END), 0)
			)
			FROM Donation d
			WHERE d.status = 'PAID'
			  AND (CAST(:startDate AS timestamp) IS NULL OR d.paidAt >= :startDate)
			  AND (CAST(:endDate AS timestamp) IS NULL OR d.paidAt < :endDate)
			""")
	OrgCollectionInfo getDonationCollectionSum(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

}
