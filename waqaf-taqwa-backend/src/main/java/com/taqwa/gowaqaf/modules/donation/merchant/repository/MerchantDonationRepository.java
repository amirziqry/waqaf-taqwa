package com.taqwa.gowaqaf.modules.donation.merchant.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.merchant.entity.MerchantDonation;

public interface MerchantDonationRepository extends JpaRepository<MerchantDonation, UUID> {

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM MerchantDonation d
			WHERE d.status = 'PAID'
			AND (:startDate IS NULL OR d.paidAt >= :startDate)
			AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM MerchantDonation d
			    WHERE d.merchant.id = :id
			      AND d.status = 'PAID'
			""")
	BigDecimal sumPaidDonationsById(@Param("id") UUID id);

}
