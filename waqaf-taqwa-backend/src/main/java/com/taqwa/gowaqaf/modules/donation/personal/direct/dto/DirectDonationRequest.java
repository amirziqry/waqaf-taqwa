package com.taqwa.gowaqaf.modules.donation.personal.direct.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectDonationRequest {

	private BigDecimal amount;

	private String redirectUrl;

}
