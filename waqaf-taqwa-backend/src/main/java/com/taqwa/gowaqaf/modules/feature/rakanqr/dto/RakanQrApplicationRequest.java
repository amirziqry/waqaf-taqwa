package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrApplicationRequest {

	private RakanQrType type;

	private String fullName;

	private String icNumber;

	private String phone;

	private String representativeType;

	private String establishmentName;

	private String qrSpot;

	private String postalAddress;

	private String bankName;

	private String bankAccountNumber;

}
