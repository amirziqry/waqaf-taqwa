package com.taqwa.gowaqaf.modules.user.merchant.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountInfo {

	private UUID id;

	private String username;

	private String accountHolderName;

	private String email;

	private String phone;

	private Boolean modMesra;

}
