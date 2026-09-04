package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.qr;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenCreateQrRequest {

	private BigDecimal fieldAmount;
	private String fieldPaymentDescription;
	private String fieldCallbackUrl;
	private String fieldExternalReferenceLabel1;
	private String fieldExternalReferenceValue1;
	private String fieldExternalReferenceLabel2;
	private String fieldExternalReferenceValue2;

}
