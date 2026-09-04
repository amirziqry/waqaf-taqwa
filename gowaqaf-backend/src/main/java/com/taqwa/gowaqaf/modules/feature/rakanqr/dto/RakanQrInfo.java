package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrType;

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

	private String email;

}
