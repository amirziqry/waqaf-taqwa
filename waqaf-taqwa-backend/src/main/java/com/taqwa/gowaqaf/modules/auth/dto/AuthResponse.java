package com.taqwa.gowaqaf.modules.auth.dto;

import com.taqwa.gowaqaf.modules.user.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String username;
	
	private Role role;

}
