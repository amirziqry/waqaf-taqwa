package com.taqwa.gowaqaf.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCredentials {

	private String username;

	private String password;

	private String email;

	private String phone;

	private String accountHolderName;

	private Boolean modMesra;

}
