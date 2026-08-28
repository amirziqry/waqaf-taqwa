package com.taqwa.gowaqaf.modules.user.admin.mapper;

import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;

public class AdminMapper {

	public static AdminRegisterResponse mapToRegisterResponse(Admin admin) {
		return new AdminRegisterResponse(admin.getUsername(), admin.getIdentity().getEmail(),
				admin.getIdentity().getPhone(), admin.getRoles());
	}

	public static AdminInfo mapToAdminInfo(Admin admin) {
		AdminInfo dto = new AdminInfo();
		dto.setId(admin.getId());
		dto.setUsername(admin.getUsername());
		dto.setRoles(admin.getRoles());
		dto.setEmail(admin.getIdentity().getEmail());
		dto.setPhone(admin.getIdentity().getPhone());
		dto.setModMesra(admin.getIdentity().getModMesra());

		return dto;
	}

}
