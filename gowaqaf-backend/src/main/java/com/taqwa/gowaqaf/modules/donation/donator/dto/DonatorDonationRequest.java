package com.taqwa.gowaqaf.modules.donation.donator.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonatorDonationRequest {

	private BigDecimal amount;

}
