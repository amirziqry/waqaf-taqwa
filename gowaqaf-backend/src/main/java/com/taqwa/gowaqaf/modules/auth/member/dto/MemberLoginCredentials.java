package com.taqwa.gowaqaf.modules.auth.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginCredentials {

	private String username;
	private String password;

}
