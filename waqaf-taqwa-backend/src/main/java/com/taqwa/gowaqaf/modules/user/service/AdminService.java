package com.taqwa.gowaqaf.modules.user.service;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.dto.admin.UpdateAdminRoleRequest;

public interface AdminService {

	RegisterResponse createEditor(RegisterCredentials dto);

	RegisterResponse createAdmin(RegisterCredentials dto);

	List<AccountInfo> getAllAdmins();

	void updateAdminRoleById(UUID userId, UpdateAdminRoleRequest dto);

	void deleteAdminById(UUID userId);

}
