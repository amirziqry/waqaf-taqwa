package com.taqwa.gowaqaf.modules.auth.member.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginResponse {

	private String username;
	private String email;
	private Set<String> roles;

}
