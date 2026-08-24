package com.taqwa.gowaqaf.modules.organization.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private Long postcode;
	private String city;
	private String state;
	private String country;

}
