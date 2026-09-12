package com.taqwa.gowaqaf.modules.donation.rakanqr.repository;

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

import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;

public interface RakanQrDonationRepository extends JpaRepository<RakanQrDonation, UUID> {

	Page<RakanQrDonation> findByRakanQrId(UUID rakanQrId, Pageable pageable);

	Optional<RakanQrDonation> findByDonation_WebhookToken(String token);

	@Query("""
			SELECT COALESCE(SUM(d.donation.amount), 0)
			FROM RakanQrDonation d
			WHERE d.rakanQr.id = :id
			  AND d.donation.status = 'PAID'
			  AND (CAST(:startDate AS timestamp) IS NULL OR d.donation.paidAt >= :startDate)
			  AND (CAST(:endDate AS timestamp) IS NULL OR d.donation.paidAt <= :endDate)
			""")
	BigDecimal sumAllPaidDonationsByUser(@Param("id") UUID rakanQrId, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("""
			SELECT COALESCE(SUM(d.donation.amount), 0)
			FROM RakanQrDonation d
			WHERE d.donation.status = 'PAID'
			  AND (CAST(:startDate AS timestamp) IS NULL OR d.donation.paidAt >= :startDate)
			  AND (CAST(:endDate AS timestamp) IS NULL OR d.donation.paidAt < :endDate)
			""")
	BigDecimal sumAllPaidDonations(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	List<RakanQrDonation> findAllByRakanQr_Id(UUID rakanQrId, Pageable pageable);

	Optional<RakanQrDonation> findByDonation_BillingCode(String billingCode);

}
