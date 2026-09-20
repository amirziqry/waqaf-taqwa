package com.taqwa.gowaqaf.modules.donation.personal.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PersonalDonationSum(@JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00") BigDecimal total) {

}
