package com.taqwa.gowaqaf.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

	private String name;
	private BigDecimal amount;
	private String redirectUrl;
	private String callbackUrl;
	private String collectionCode;

}
