package com.taqwa.gowaqaf.modules.user.admin.entity;

import java.util.Set;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String username;

	@Column
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "identity_id", nullable = true, unique = true)
	private AccountInfo info;

}
