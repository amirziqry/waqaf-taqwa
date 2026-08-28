package com.taqwa.gowaqaf.modules.user.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRegisterCredentials {

	private String username;

	private String password;

	private String email;

	private String phone;

	private Boolean modMesra;

}
