package com.taqwa.gowaqaf.external.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

	private String collectionCode;
	private String name;
	private String email;
	private String phone;
	private BigDecimal amount;
	private String description;
	private String redirectUrl;
	private String callbackUrl;

}
