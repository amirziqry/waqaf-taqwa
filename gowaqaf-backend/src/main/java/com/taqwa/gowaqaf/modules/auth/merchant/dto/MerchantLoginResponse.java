package com.taqwa.gowaqaf.modules.auth.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantLoginResponse {

	private String username;
	private String email;

}
