package com.taqwa.gowaqaf.modules.user.admin.dto;

import java.util.Set;

import com.taqwa.gowaqaf.modules.user.admin.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterResponse {

	private String username;
	
	private String email;
	
	private String phone;
	
	private Set<Role> roles;

}
