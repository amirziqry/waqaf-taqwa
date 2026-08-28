package com.taqwa.gowaqaf.modules.organization.collection.dto;

import java.math.BigDecimal;

public record OrganizationCollectionSum(BigDecimal personalDirectSum, BigDecimal personalRecurringSum,
		BigDecimal projectSum, BigDecimal merchantDirectSum, BigDecimal rakanQrSum, BigDecimal total) {

}
