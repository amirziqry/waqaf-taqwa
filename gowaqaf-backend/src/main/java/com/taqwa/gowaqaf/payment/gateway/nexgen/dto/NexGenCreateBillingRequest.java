package com.taqwa.gowaqaf.payment.gateway.nexgen.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenCreateBillingRequest {

	private String fieldName;
	private String fieldEmail;
	private Long fieldPhone;
	private BigDecimal fieldAmount;
	private String fieldPaymentDescription;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String fieldDueDate;
	private String fieldRedirectUrl;
	private String fieldCallbackUrl;
	private String fieldExternalReferenceLabel1;
	private String fieldExternalReferenceValue1;
	private String fieldExternalReferenceLabel2;
	private String fieldExternalReferenceValue2;
	private String fieldExternalReferenceLabel3;
	private String fieldExternalReferenceValue3;
	private String fieldExternalReferenceLabel4;
	private String fieldExternalReferenceValue4;

}
