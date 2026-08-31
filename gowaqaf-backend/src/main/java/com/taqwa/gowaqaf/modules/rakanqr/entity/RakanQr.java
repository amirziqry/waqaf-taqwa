package com.taqwa.gowaqaf.modules.rakanqr.entity;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rakan_qr_agent_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RakanQr {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, nullable = true)
	private String code;

	@Enumerated(EnumType.STRING)
	private RakanQrType type;

	@Enumerated(EnumType.STRING)
	private RakanQrStatus status;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "personal_id", nullable = true, unique = true)
	private Personal personal;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "merchant_id", nullable = true, unique = true)
	private Merchant merchant;

}
