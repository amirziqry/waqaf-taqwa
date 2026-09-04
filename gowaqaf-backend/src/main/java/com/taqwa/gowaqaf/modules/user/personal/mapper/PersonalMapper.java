package com.taqwa.gowaqaf.modules.user.personal.mapper;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

public class PersonalMapper {

	public static PersonalRegisterResponse mapToRegisterResponse(Personal personal) {
		return new PersonalRegisterResponse(personal.getUsername(), personal.getInfo().getEmail(),
				personal.getInfo().getPhone());
	}

	public static PersonalAccountInfo mapToAccountInfo(Personal personal) {
		PersonalAccountInfo dto = new PersonalAccountInfo();
		dto.setId(personal.getId());
		dto.setUsername(personal.getUsername());
		dto.setAccountHolderName(personal.getInfo().getAccountHolderName());
		dto.setEmail(personal.getInfo().getEmail());
		dto.setPhone(personal.getInfo().getPhone());
		dto.setModMesra(personal.getInfo().getModMesra());

		return dto;
	}

}
