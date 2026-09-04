package com.taqwa.gowaqaf.modules.donation.personal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

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
@Table(name = "personal_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDonation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "personal_id", nullable = true)
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;

	@Column
	private String name;

	@Column(nullable = true, unique = true)
	private String billingCode;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = true)
	private String transactionId;

	@Column
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime paidAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DonationType donationType;

	@Column(nullable = false)
	private Boolean taxExempt;

	@Column(unique = true, nullable = true)
	private String receiptHashId;

	@Column(nullable = true, unique = true)
	private String webhookToken;

}
