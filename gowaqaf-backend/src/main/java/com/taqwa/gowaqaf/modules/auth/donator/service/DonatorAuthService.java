package com.taqwa.gowaqaf.modules.auth.donator.service;

import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorLoginCredentials;

public interface DonatorAuthService {

	DonatorAuthDetails login(DonatorLoginCredentials request);

}
