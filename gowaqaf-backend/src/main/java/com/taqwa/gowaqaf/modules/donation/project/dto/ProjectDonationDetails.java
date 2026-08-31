package com.taqwa.gowaqaf.modules.donation.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDonationDetails {

	private UUID id;

	private String billingCode;

	private BigDecimal amount;

	private LocalDateTime paidAt;

	private PaymentStatus status;

	private String receiptHashId;

	private UUID projectId;

	private String projectName;

}
