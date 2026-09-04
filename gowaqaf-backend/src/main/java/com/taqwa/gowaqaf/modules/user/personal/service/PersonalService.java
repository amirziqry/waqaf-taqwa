package com.taqwa.gowaqaf.modules.user.personal.service;

import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface PersonalService {

	PersonalRegisterResponse createPersonal(PersonalRegisterCredentials request);

	Personal getPersonalByUsername(String username);

	void updateAccountByUser(AccountUserDetails principal, AccountUploadFields request);

	PersonalAccountInfo getAccountByUser(AccountUserDetails principal);

}
