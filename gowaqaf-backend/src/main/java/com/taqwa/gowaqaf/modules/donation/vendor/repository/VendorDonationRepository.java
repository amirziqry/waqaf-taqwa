package com.taqwa.gowaqaf.modules.donation.vendor.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taqwa.gowaqaf.modules.donation.vendor.entity.VendorDonation;

public interface VendorDonationRepository extends JpaRepository<VendorDonation, UUID> {

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM VendorDonation d
			    WHERE d.status = 'PAID'
			""")
	BigDecimal sumAllPaidDonations();

	@Query("""
			    SELECT COALESCE(SUM(d.amount), 0)
			    FROM VendorDonation d
			    WHERE d.vendor.id = :id
			      AND d.status = 'PAID'
			""")
	BigDecimal sumPaidDonationsById(@Param("id") UUID id);

}
