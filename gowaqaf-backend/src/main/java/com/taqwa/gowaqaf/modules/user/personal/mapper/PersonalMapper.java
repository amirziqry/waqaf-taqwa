package com.taqwa.gowaqaf.modules.user.personal.mapper;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

public class PersonalMapper {

	public static PersonalRegisterResponse mapToRegisterResponse(Personal personal) {
		return new PersonalRegisterResponse(personal.getUsername(), personal.getInfo().getEmail(),
				personal.getInfo().getPhone());
	}

}
