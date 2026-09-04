package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NexGenWebhookPayload {

	private String code;
	private String status;
	private BigDecimal amount;

	private String externalReferenceLabel1;
	private String externalReferenceValue1;

	private String externalReferenceLabel2;
	private String externalReferenceValue2;

	private String externalReferenceLabel3;
	private String externalReferenceValue3;

	private String externalReferenceLabel4;
	private String externalReferenceValue4;

	private NexGenPaymentMethodDetail paymentMethodDetail;

}
