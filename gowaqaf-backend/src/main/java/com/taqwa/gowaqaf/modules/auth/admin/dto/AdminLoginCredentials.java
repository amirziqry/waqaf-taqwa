package com.taqwa.gowaqaf.modules.auth.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginCredentials {

	private String username;
	private String password;

}
