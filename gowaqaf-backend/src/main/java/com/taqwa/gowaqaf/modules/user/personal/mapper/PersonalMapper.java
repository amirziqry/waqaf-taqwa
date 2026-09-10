package com.taqwa.gowaqaf.modules.user.personal.mapper;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

public class PersonalMapper {

	public static PersonalRegisterResponse mapToRegisterResponse(Personal user) {
		return new PersonalRegisterResponse(user.getUsername(), user.getInfo().getEmail(),
				user.getInfo().getPhone());
	}

	public static PersonalAccountInfo mapToAccountInfo(Personal user) {
		PersonalAccountInfo dto = new PersonalAccountInfo();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setAccountHolderName(user.getInfo().getAccountHolderName());
		dto.setEmail(user.getInfo().getEmail());
		dto.setPhone(user.getInfo().getPhone());
		dto.setModMesra(user.getInfo().getModMesra());

		return dto;
	}

}
