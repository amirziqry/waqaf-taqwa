package com.taqwa.gowaqaf.modules.user.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRegisterResponse {

	private String username;
	private String email;

}
