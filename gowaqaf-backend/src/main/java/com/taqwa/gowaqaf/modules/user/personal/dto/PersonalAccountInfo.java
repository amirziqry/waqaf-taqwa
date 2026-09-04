package com.taqwa.gowaqaf.modules.user.personal.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAccountInfo {

	private UUID id;

	private String username;

	private String accountHolderName;

	private String email;

	private String phone;

	private Boolean modMesra;

}
