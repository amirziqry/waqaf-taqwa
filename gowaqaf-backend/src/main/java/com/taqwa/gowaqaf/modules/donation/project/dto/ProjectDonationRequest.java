package com.taqwa.gowaqaf.modules.donation.project.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDonationRequest {

	private BigDecimal amount;

}
