package com.taqwa.gowaqaf.modules.donation.personal.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.user.entity.Account;

import jakarta.persistence.CascadeType;
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
@Table(name = "personal_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDonation {

	@Id
	private UUID id;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, optional = false)
	@JoinColumn(name = "id")
	private Transaction transaction;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "personal_id", nullable = true)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;

}
