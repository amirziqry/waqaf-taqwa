package com.taqwa.gowaqaf.modules.user.personal.service;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;

public interface PersonalService {

	PersonalRegisterResponse createPersonal(PersonalRegisterCredentials request);

}
