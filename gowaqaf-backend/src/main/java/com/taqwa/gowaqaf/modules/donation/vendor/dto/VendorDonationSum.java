package com.taqwa.gowaqaf.modules.donation.vendor.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public record VendorDonationSum(@JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00") BigDecimal total) {

}
