package com.taqwa.gowaqaf.modules.auth.admin.service;

import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminAuthDetails;
import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminLoginCredentials;

public interface AdminAuthService {

	AdminAuthDetails login(AdminLoginCredentials request);

}
