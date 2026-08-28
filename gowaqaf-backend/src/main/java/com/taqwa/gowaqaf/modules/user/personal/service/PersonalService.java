package com.taqwa.gowaqaf.modules.user.personal.service;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;

public interface PersonalService {

	PersonalRegisterResponse createPersonal(PersonalRegisterCredentials request);

	Personal getPersonalByUsername(String username);

}
