package com.taqwa.gowaqaf.modules.donation.agent.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.agent.entity.RakanQrDonation;

public interface RakanQrDonationRepository extends JpaRepository<RakanQrDonation, UUID> {

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM RakanQrDonation d
			WHERE d.rakanQr.id = :id
			AND d.status = 'PAID'
			AND (:startDate IS NULL OR d.paidAt >= :startDate)
			AND (:endDate IS NULL OR d.paidAt <= :endDate)
			""")
	BigDecimal sumPaidDonationsByAgent(@Param("id") UUID id, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM RakanQrDonation d
			WHERE d.status = 'PAID'
			  AND (:startDate IS NULL OR d.paidAt >= :startDate)
			  AND (:endDate IS NULL OR d.paidAt < :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

}
