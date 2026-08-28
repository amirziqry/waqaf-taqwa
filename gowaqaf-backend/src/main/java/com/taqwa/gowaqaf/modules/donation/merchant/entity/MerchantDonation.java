package com.taqwa.gowaqaf.modules.donation.merchant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.donation.personal.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.personal.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;

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
@Table(name = "merchant_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDonation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "merchant_id", nullable = true)
	private Merchant merchant;

	@Column
	private String payerName;

	@Column(nullable = false, unique = true)
	private String billingCode;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime paidAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DonationType donationType;

}
