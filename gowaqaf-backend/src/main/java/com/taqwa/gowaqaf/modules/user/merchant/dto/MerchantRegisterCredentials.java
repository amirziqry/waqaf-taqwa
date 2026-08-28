package com.taqwa.gowaqaf.modules.user.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRegisterCredentials {

	private String username;

	private String password;

	private String email;

	private String phone;

	private Boolean modMesra;

}
