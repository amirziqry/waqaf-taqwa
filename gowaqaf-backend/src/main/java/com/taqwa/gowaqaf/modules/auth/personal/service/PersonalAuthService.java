package com.taqwa.gowaqaf.modules.auth.personal.service;

import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalAuthDetails;
import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalLoginCredentials;

public interface PersonalAuthService {

	PersonalAuthDetails login(PersonalLoginCredentials request);

}
