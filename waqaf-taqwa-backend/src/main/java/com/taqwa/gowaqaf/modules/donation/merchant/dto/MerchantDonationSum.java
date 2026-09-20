package com.taqwa.gowaqaf.modules.donation.merchant.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MerchantDonationSum(@JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00") BigDecimal total) {

}
