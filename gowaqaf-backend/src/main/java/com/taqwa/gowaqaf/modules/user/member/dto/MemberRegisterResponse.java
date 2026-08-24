package com.taqwa.gowaqaf.modules.user.member.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterResponse {

	private String username;
	private String email;
	private Set<String> roles;

}
