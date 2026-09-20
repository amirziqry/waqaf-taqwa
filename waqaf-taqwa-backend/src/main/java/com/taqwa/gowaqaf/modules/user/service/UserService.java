package com.taqwa.gowaqaf.modules.user.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.entity.Account;

public interface UserService {

	RegisterResponse createUser(RegisterCredentials dto);

	void updateUserInfoById(UUID userId, AccountUploadFields request);

	Account getUserById(UUID userId);

	AccountInfo getUserDetailsById(UUID userId);

	void changeUserPassword(UUID userId, ChangePasswordRequest request);

}
