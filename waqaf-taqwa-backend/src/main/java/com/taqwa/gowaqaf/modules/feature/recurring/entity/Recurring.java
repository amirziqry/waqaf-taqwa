package com.taqwa.gowaqaf.modules.feature.recurring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.recurring.enums.FrequencyType;
import com.taqwa.gowaqaf.modules.user.entity.Account;

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
@Table(name = "recurring_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recurring {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "account_id", nullable = true)
	private Account account;

	@Column(nullable = false, precision = 19, scale = 2)
	BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	FrequencyType frequency;

	@Column(nullable = false)
	private LocalDateTime nextPaymentAt;

	@Column(nullable = false)
	Boolean autoRoundUp;

}
