package com.taqwa.gowaqaf.modules.organization.collection.dto;

import java.math.BigDecimal;

public record OrgCollectionInfo(BigDecimal directTotal, BigDecimal recurringTotal, BigDecimal projectTotal,
		BigDecimal merchantTotal, BigDecimal rakanQrTotal) {

}
