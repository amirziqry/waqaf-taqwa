package com.taqwa.gowaqaf.payment.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlResponse {

	private UUID id;
	private String billingCode;
	private String status;
	private String paymentUrl;

}
