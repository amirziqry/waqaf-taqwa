package com.taqwa.gowaqaf.modules.user.admin.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.dto.UpdateAdminRoleRequest;

public interface AdminService {

	AdminRegisterResponse createEditor(AdminRegisterCredentials request);

	AdminRegisterResponse createAdmin(AdminRegisterCredentials request);

	AdminInfo getAdminByUsername(String username);

	List<AdminInfo> getAllAdmins();

	void updateAdminRole(String username, UpdateAdminRoleRequest dto);

	void deleteAdminByUsername(String username);

}
