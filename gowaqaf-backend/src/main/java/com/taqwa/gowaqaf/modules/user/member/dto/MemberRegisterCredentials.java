package com.taqwa.gowaqaf.modules.user.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterCredentials {

	private String username;
	private String email;
	private String password;

}
