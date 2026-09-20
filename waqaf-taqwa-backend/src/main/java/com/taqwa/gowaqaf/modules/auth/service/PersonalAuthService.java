package com.taqwa.gowaqaf.modules.auth.service;

import com.taqwa.gowaqaf.modules.auth.dto.AuthDetails;
import com.taqwa.gowaqaf.modules.auth.dto.LoginCredentials;

public interface PersonalAuthService {

	AuthDetails login(LoginCredentials request);

}
