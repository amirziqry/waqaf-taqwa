package com.taqwa.gowaqaf.modules.user.admin.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.entity.Role;

public class AdminMapper {

	public static AdminRegisterResponse mapToRegisterResponse(Admin admin) {
		Set<String> roles = admin.getRoles().stream().map(Role::name).collect(Collectors.toSet());

		return new AdminRegisterResponse(admin.getUsername(), admin.getEmail(), roles);
	}

	public static AdminInfo mapToAdminInfo(Admin admin) {

		return new AdminInfo(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getRoles());
	}

}
