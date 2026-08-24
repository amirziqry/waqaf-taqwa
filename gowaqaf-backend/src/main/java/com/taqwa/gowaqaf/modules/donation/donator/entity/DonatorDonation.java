package com.taqwa.gowaqaf.modules.donation.donator.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.project.entity.Project;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;

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
@Table(name = "donator_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonatorDonation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "donator_id", nullable = false)
	private Donator donator;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;

	@Column
	private String name;

	@Column(nullable = false, unique = true)
	private String billingCode;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DonationType donationType;

}
