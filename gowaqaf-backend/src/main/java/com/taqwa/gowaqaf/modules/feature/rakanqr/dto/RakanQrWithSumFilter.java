package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrWithSumFilter {

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate endDate;

}
