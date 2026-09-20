package com.taqwa.gowaqaf.modules.feature.rakanqr.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.user.entity.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "rakanqrs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RakanQr {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, nullable = false)
	private String code;

	@Enumerated(EnumType.STRING)
	private RakanQrType type;

	@Enumerated(EnumType.STRING)
	private RakanQrStatus status;

	@Column
	private String fullName;

	@Column(unique = false)
	private String icNumber;

	@Column(unique = false)
	private String phone;

	@Column(unique = false)
	private String representativeType;

	@Column(unique = false)
	private String establishmentName;

	@Column(unique = false)
	private String qrSpot;

	@Column(unique = false)
	private String postalAddress;

	@Column(unique = false)
	private String bankName;

	@Column(unique = false)
	private String bankAccountNumber;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "account_id", nullable = true, unique = true)
	private Account account;

	@Column(unique = true, nullable = true)
	private String collectionCode;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal collectedAmount;

	@Column(nullable = true, precision = 19, scale = 2)
	private BigDecimal commission;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

}
