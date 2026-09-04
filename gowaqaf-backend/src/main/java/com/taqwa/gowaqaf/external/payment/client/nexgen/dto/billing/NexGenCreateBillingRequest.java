package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenCreateBillingRequest {

	private String fieldName;
	private String fieldEmail;
	private String fieldPhone;
	private BigDecimal fieldAmount;
	private String fieldPaymentDescription;
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
