package com.taqwa.gowaqaf.modules.user.mapper;

import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;

public class AccountMapper {

	public static RegisterResponse mapToRegisterResponse(Account user) {

		return new RegisterResponse(user.getUsername(), user.getEmail(), user.getPhone(), user.getRoles());
	}

	public static AccountInfo mapToAccountInfo(Account user) {
		AccountInfo dto = new AccountInfo();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setAccountHolderName(user.getAccountHolderName());
		dto.setEmail(user.getEmail());
		dto.setPhone(user.getPhone());
		dto.setModMesra(user.getModMesra());
		dto.setRole(user.getRoles().stream().map(Role::name).findFirst()
				.orElseThrow(() -> new IllegalStateException("Account has no role")));
		dto.setCreatedAt(user.getCreatedAt());

		return dto;
	}

}
