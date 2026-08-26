package com.taqwa.gowaqaf.modules.auth.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalLoginCredentials {

	private String username;
	private String password;

}
