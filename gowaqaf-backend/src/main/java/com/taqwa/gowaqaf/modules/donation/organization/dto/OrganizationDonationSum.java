package com.taqwa.gowaqaf.modules.donation.organization.dto;

import java.math.BigDecimal;

public record OrganizationDonationSum(BigDecimal donatorTotal, BigDecimal vendorTotal, BigDecimal total) {

}
