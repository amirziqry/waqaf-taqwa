package com.taqwa.gowaqaf.modules.user.donator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonatorRegisterResponse {

	private String username;
	private String email;

}
