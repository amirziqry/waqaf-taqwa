package com.taqwa.gowaqaf.modules.donation.donator.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DonatorDonationSum(@JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00") BigDecimal total) {

}
