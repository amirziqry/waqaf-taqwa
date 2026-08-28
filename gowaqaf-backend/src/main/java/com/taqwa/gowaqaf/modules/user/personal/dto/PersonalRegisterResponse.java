package com.taqwa.gowaqaf.modules.user.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRegisterResponse {

	private String username;

	private String email;

	private String phone;

}
