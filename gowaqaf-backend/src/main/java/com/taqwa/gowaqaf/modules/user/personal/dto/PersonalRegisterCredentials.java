package com.taqwa.gowaqaf.modules.user.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRegisterCredentials {

	private String username;
	private String email;
	private String password;

}
