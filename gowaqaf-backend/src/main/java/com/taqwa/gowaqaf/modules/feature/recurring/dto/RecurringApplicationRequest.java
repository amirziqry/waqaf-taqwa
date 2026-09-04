package com.taqwa.gowaqaf.modules.feature.recurring.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.recurring.enums.FrequencyType;
import com.taqwa.gowaqaf.modules.feature.recurring.enums.RecurringType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringApplicationRequest {

	BigDecimal amount;

	RecurringType type;

	FrequencyType frequency;

	UUID projectId;

	Boolean autoRoundUp;

}
