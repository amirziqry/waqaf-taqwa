package com.taqwa.gowaqaf.modules.auth.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorLoginResponse {

	private String username;
	private String email;

}
