package com.taqwa.gowaqaf.modules.donation.personal.dto;

import java.math.BigDecimal;

public record PersonalCollectionSum(BigDecimal directTotal, BigDecimal recurringTotal, BigDecimal projectTotal) {

}
