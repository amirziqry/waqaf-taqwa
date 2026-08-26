package com.taqwa.gowaqaf.modules.user.merchant.service;

import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;

public interface MerchantService {

	MerchantRegisterResponse createMerchant(MerchantRegisterCredentials request);

}
