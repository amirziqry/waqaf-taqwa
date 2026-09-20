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

	private String referenceLabel1;
	private String referenceValue1;
	private String referenceLabel2;
	private String referenceValue2;
	private String referenceLabel3;
	private String referenceValue3;
	private String referenceLabel4;
	private String referenceValue4;

}
