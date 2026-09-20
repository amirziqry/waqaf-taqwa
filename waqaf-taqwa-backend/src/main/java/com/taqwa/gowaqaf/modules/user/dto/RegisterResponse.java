package com.taqwa.gowaqaf.modules.user.dto;

import java.util.Set;

import com.taqwa.gowaqaf.modules.user.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

	private String username;

	private String email;

	private String phone;

	private Set<Role> roles;

}
