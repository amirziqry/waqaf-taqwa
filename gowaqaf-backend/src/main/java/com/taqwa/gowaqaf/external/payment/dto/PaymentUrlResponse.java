package com.taqwa.gowaqaf.external.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlResponse {

	private UUID id;
	
	private String billingCode;

	private BigDecimal amount;

	private String status;

	private String paymentUrl;

}
