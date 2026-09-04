package com.taqwa.gowaqaf.modules.user.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUploadFields {

	private String accountHolderName;

	private String phone;

	private String email;

	private Boolean modMesra;

}
