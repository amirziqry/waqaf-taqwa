package com.taqwa.gowaqaf.modules.auth.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalLoginResponse {

	private String username;
	private String email;

}
