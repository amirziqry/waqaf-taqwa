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

import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface PersonalDonationRepository extends JpaRepository<PersonalDonation, UUID> {
	Optional<PersonalDonation> findByIdAndAccountId(UUID id, UUID personalId);

	Optional<PersonalDonation> findByTransaction_WebhookToken(String token);

	Optional<PersonalDonation> findByTransaction_BillingCode(String billingCode);

	Optional<PersonalDonation> findByAccount_IdAndTransaction_BillingCode(UUID personalId, String billingCode);

	Page<PersonalDonation> findByAccountId(UUID personalId, Pageable pageable);

	List<PersonalDonation> findAllByAccountId(UUID personalId, Pageable pageable);

	@Query("""
			SELECT COALESCE(SUM(pd.transaction.amount), 0)
			FROM PersonalDonation pd
			WHERE pd.account.id = :personalId
			  AND pd.transaction.status = 'PAID'
			  AND (CAST(:startDate AS timestamp) IS NULL OR pd.transaction.paidAt >= :startDate)
			  AND (CAST(:endDate AS timestamp) IS NULL OR pd.transaction.paidAt < :endDate)
			""")
	BigDecimal sumPaidDonationsByAccountId(@Param("personalId") UUID personalId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
