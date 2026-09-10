package com.taqwa.gowaqaf.modules.feature.rakanqr.dto;

import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrApplicationRequest {

	RakanQrType type;

}
