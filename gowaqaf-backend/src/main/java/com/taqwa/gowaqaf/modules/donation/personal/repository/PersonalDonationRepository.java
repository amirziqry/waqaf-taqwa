package com.taqwa.gowaqaf.modules.donation.personal.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface PersonalDonationRepository extends JpaRepository<PersonalDonation, UUID> {
	Optional<PersonalDonation> findByIdAndPersonalId(UUID id, UUID personalId);

	Optional<PersonalDonation> findByDonation_WebhookToken(String token);

	Optional<PersonalDonation> findByDonation_BillingCode(String billingCode);

	Optional<PersonalDonation> findByPersonal_IdAndDonation_BillingCode(UUID personalId, String billingCode);

	Page<PersonalDonation> findByPersonalId(UUID personalId, Pageable pageable);

	List<PersonalDonation> findAllByPersonalId(UUID personalId, Pageable pageable);

	@Query("""
			SELECT COALESCE(SUM(d.donation.amount), 0)
			FROM PersonalDonation d
			WHERE d.donation.status = 'PAID'
			  AND (:startDate IS NULL OR d.donation.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.donation.paidAt < :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT new com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum(
			    COALESCE(SUM(CASE WHEN d.donation.donationType = 'DIRECT' THEN d.donation.amount ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN d.donation.donationType = 'RECURRING' THEN d.donation.amount ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN d.donation.donationType = 'PROJECT' THEN d.donation.amount ELSE 0 END), 0)
			)
			FROM PersonalDonation d
			WHERE d.donation.status = 'PAID'
			  AND (:startDate IS NULL OR d.donation.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.donation.paidAt < :endDate)
			""")
	PersonalCollectionSum sumPaidDonationsByType(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT COALESCE(SUM(amount), 0)
			FROM (
			    SELECT pd.donation.amount AS amount
			    FROM PersonalDonation pd
			    WHERE pd.personal.id = :personalId
			      AND pd.donation.status = 'PAID'
			      AND (CAST(:startDate AS timestamp) IS NULL OR pd.donation.paidAt >= :startDate)
			      AND (CAST(:endDate AS timestamp) IS NULL OR pd.donation.paidAt < :endDate)

			    UNION ALL

			    SELECT pd.donation.amount AS amount
			    FROM ProjectDonation pd
			    WHERE pd.personal.id = :personalId
			      AND pd.donation.status = 'PAID'
			      AND (CAST(:startDate AS timestamp) IS NULL OR pd.donation.paidAt >= :startDate)
			      AND (CAST(:endDate AS timestamp) IS NULL OR pd.donation.paidAt < :endDate)
			)
			""")
	BigDecimal sumPaidDonationsByPersonalId(@Param("personalId") UUID personalId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
