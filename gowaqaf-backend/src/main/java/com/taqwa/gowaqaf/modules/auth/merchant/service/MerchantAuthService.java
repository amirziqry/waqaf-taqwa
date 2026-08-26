package com.taqwa.gowaqaf.modules.auth.merchant.service;

import com.taqwa.gowaqaf.modules.auth.merchant.dto.MerchantAuthDetails;
import com.taqwa.gowaqaf.modules.auth.merchant.dto.MerchantLoginCredentials;

public interface MerchantAuthService {

	MerchantAuthDetails login(MerchantLoginCredentials request);

}
