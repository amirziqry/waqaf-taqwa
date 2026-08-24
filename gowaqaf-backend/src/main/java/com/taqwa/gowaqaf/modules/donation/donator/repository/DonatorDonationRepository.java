package com.taqwa.gowaqaf.modules.donation.donator.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.donator.entity.DonatorDonation;

public interface DonatorDonationRepository extends JpaRepository<DonatorDonation, UUID> {

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM DonatorDonation d
			    WHERE d.status = 'PAID'
			""")
	BigDecimal sumAllPaidDonations();

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM DonatorDonation d
			    WHERE d.donator.id = :id
			      AND d.status = 'PAID'
			""")
	BigDecimal sumPaidDonationsById(@Param("id") UUID id);

	@Query("""
			SELECT COALESCE(SUM(d.amount), 0)
			FROM DonatorDonation d
			WHERE d.project.id = :id
			  AND d.status = 'PAID'
			  AND d.donationType = 'PROJECT'
			""")
	BigDecimal sumPaidDonationsByProjectId(@Param("id") UUID id);

}
