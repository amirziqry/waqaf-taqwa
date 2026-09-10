package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrWithCollection {

	private UUID id;

	private String code;

	private RakanQrType type;

	private RakanQrStatus status;

	private BigDecimal collected;

}
