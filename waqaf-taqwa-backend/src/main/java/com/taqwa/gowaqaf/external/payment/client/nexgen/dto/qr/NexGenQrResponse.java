package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NexGenQrResponse {

	private String code;
	private String status;
	private BigDecimal amount;
	private String paymentDescription;
	@JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss")
	private String dueDate;
	private String externalReferenceLabel1;
	private String externalReferenceValue1;
	private String externalReferenceLabel2;
	private String externalReferenceValue2;
	private String callbackUrl;
	private String qrCode;

}
