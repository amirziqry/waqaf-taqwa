package com.taqwa.gowaqaf.modules.donation.personal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDonationDetails {

	private UUID id;

	private String billingCode;

	private String transactionId;

	private BigDecimal amount;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime paidAt;

	private PaymentStatus status;

	private String receiptHashId;

}
