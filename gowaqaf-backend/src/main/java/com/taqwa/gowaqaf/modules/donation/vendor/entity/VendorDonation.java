package com.taqwa.gowaqaf.modules.donation.vendor.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.donator.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorDonation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column
	private String payerName;

	@Column(nullable = false, unique = true)
	private String billingCode;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

}
