package com.taqwa.gowaqaf.modules.user.donator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonatorRegisterCredentials {

	private String username;
	private String email;
	private String password;

}
