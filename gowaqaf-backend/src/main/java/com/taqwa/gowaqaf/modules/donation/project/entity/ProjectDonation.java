package com.taqwa.gowaqaf.modules.donation.project.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
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
@Table(name = "project_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDonation {

	@Id
	private UUID id;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	@JoinColumn(name = "id")
	private Donation donation;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "personal_id", nullable = true)
	private Personal personal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "merchant_id", nullable = true)
	private Merchant merchant;

	@Column(nullable = false)
	private Boolean taxExempt;

	@Column(unique = true, nullable = true)
	private String receiptHashId;

}
