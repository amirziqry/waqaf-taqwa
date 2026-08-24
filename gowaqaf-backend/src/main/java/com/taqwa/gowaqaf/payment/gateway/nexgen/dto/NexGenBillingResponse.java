package com.taqwa.gowaqaf.payment.gateway.nexgen.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenBillingResponse {

	private String code;
	private String status;
	private BigDecimal amount;
	private String paymentDescription;
	@JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss")
	private String dueDate;
	private String payerName;
	private String payerEmail;
	private Long payerPhone;
	private String externalReferenceLabel1;
	private String externalReferenceValue1;
	private String externalReferenceLabel2;
	private String externalReferenceValue2;
	private String externalReferenceLabel3;
	private String externalReferenceValue3;
	private String externalReferenceLabel4;
	private String externalReferenceValue4;
	private String redirectUrl;
	private String callbackUrl;
	private String paymentUrl;

}
