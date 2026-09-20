package com.taqwa.gowaqaf.modules.feature.recurring.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.recurring.enums.FrequencyType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringApplicationRequest {

	BigDecimal amount;

	FrequencyType frequency;

	UUID projectId;

	Boolean autoRoundUp;

}
