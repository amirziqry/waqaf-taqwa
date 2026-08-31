package com.taqwa.gowaqaf.modules.donation.personal.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface PersonalDonationRepository extends JpaRepository<PersonalDonation, UUID> {

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.status = 'PAID'
			AND (:startDate IS NULL OR d.paidAt >= :startDate)
			AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum(
			    COALESCE(SUM(CASE WHEN d.donationType = 'DIRECT' THEN d.amount ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN d.donationType = 'RECURRING' THEN d.amount ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN d.donationType = 'PROJECT' THEN d.amount ELSE 0 END), 0)
			)
			FROM PersonalDonation d
			WHERE d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	PersonalCollectionSum sumPaidDonationsByType(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.personal.id = :personalId
			  AND d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumPaidDonationsByPersonalId(@Param("personalId") UUID personalId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.project.id = :id
			  AND d.status = 'PAID'
			  AND d.donationType = 'PROJECT'
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("id") UUID id);

	Optional<PersonalDonation> findByIdAndPersonalId(UUID id, UUID personalId);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM PersonalDonation d
			WHERE d.project.id = :projectId
			  AND d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("projectId") UUID projectId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
