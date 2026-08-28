package com.taqwa.gowaqaf.modules.organization.collection.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCollectionSumFilter {

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate endDate;

}
