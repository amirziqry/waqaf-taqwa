package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrInfo {

	private UUID id;

	private String code;

	private RakanQrType type;

	private RakanQrStatus status;

	private String fullName;

	private String icNumber;

	private String phone;

	private String representativeType;

	private String establishmentName;

	private String qrSpot;

	private String postalAddress;

	private String bankName;

	private String bankAccountNumber;

	private String collectionCode;

	private BigDecimal collectedAmount;

	private BigDecimal commission;

	private LocalDateTime createdAt;

}
