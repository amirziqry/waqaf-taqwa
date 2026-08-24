package com.taqwa.gowaqaf.modules.donation.project.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ProjectDonationSum(@JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00") BigDecimal total) {

}
