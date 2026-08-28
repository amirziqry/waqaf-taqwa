package com.taqwa.gowaqaf.modules.user.merchant.mapper;

import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;

public class MerchantMapper {

	public static MerchantRegisterResponse mapToRegisterResponse(Merchant merchant) {
		return new MerchantRegisterResponse(merchant.getUsername(), merchant.getIdentity().getEmail(),
				merchant.getIdentity().getPhone());
	}

}
