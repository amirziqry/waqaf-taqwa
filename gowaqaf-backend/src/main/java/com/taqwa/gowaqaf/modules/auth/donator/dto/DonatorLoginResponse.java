package com.taqwa.gowaqaf.modules.auth.donator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonatorLoginResponse {

	private String username;
	private String email;

}
