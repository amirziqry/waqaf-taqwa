package com.taqwa.gowaqaf.modules.user.merchant.service;

import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;

public interface MerchantService {

	MerchantRegisterResponse createMerchant(MerchantRegisterCredentials request);

	Merchant getMerchantByUsername(String username);

}
