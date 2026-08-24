package com.taqwa.gowaqaf.modules.user.donator.service;

import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterResponse;

public interface DonatorService {

	DonatorRegisterResponse createDonator(DonatorRegisterCredentials request);

}
