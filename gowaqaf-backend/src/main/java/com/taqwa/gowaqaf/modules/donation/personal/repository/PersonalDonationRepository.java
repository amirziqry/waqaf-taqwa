package com.taqwa.gowaqaf.modules.donation.personal.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface PersonalDonationRepository extends JpaRepository<PersonalDonation, UUID> {

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.status = 'PAID'
			AND (:startDate IS NULL OR d.paidAt >= :startDate)
			AND (:endDate IS NULL OR d.paidAt <= :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM PersonalDonation d
			    WHERE d.personal.id = :id
			      AND d.status = 'PAID'
			""")
	BigDecimal sumPaidDonationsById(@Param("id") UUID id);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.project.id = :id
			  AND d.status = 'PAID'
			  AND d.donationType = 'PROJECT'
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("id") UUID id);

}
