package com.taqwa.gowaqaf.modules.auth.admin.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {

	private String username;
	private String email;
	private Set<String> roles;

}
