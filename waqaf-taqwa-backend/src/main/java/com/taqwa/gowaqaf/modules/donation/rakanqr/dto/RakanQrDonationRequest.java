package com.taqwa.gowaqaf.modules.donation.rakanqr.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrDonationRequest {

	private BigDecimal amount;

	private String redirectUrl;

}
