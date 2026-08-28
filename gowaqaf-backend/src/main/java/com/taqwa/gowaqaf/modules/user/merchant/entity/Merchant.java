package com.taqwa.gowaqaf.modules.user.merchant.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountIdentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "identity_id", nullable = true, unique = true)
	private AccountIdentity identity;

}
