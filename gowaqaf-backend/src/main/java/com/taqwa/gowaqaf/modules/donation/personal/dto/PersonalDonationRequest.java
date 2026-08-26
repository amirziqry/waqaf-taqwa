package com.taqwa.gowaqaf.modules.donation.personal.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDonationRequest {

	private BigDecimal amount;

}
