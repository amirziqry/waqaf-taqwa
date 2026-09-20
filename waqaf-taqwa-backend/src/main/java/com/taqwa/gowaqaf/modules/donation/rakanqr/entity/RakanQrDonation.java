package com.taqwa.gowaqaf.modules.donation.rakanqr.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;

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
@Table(name = "rakanqr_donation_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrDonation {

	@Id
	private UUID id;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	@JoinColumn(name = "id")
	private Transaction transaction;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "rakan_qr_id", nullable = false)
	private RakanQr rakanQr;

}
