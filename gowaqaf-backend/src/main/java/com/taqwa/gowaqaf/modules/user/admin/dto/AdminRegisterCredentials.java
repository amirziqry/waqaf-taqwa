package com.taqwa.gowaqaf.modules.user.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterCredentials {

	private String username;

	private String password;

	private String email;

	private String phone;

	private Boolean modMesra;

}
