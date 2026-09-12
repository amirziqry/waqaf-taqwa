package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenPaymentMethodDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NexGenBillingObject {

	private String code;
	private String status;
	private BigDecimal amount;

	@JsonProperty("external_reference_label_1")
	private String externalReferenceLabel1;
	@JsonProperty("external_reference_value_1")
	private String externalReferenceValue1;

	@JsonProperty("external_reference_label_2")
	private String externalReferenceLabel2;
	@JsonProperty("external_reference_value_2")
	private String externalReferenceValue2;

	@JsonProperty("external_reference_label_3")
	private String externalReferenceLabel3;
	@JsonProperty("external_reference_value_3")
	private String externalReferenceValue3;

	@JsonProperty("external_reference_label_4")
	private String externalReferenceLabel4;
	@JsonProperty("external_reference_value_4")
	private String externalReferenceValue4;

	private NexGenPaymentMethodDetail paymentMethodDetail;

}
