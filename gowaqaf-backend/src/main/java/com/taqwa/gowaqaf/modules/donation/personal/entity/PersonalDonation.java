package com.taqwa.gowaqaf.modules.donation.personal.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
	private UUID id;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, optional = false)
	@JoinColumn(name = "id")
	private Donation donation;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "personal_id", nullable = true)
	private Personal personal;

	@Column(nullable = false)
	private Boolean taxExempt;

	@Column(unique = true, nullable = true)
	private String receiptHashId;

}
