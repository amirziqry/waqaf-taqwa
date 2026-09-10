package com.taqwa.gowaqaf.modules.user.merchant.mapper;

import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantAccountInfo;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;

public class MerchantMapper {

	public static MerchantRegisterResponse mapToRegisterResponse(Merchant merchant) {
		return new MerchantRegisterResponse(merchant.getUsername(), merchant.getInfo().getEmail(),
				merchant.getInfo().getPhone());
	}

	public static MerchantAccountInfo mapToAccountInfo(Merchant user) {
		MerchantAccountInfo dto = new MerchantAccountInfo();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setAccountHolderName(user.getInfo().getAccountHolderName());
		dto.setEmail(user.getInfo().getEmail());
		dto.setPhone(user.getInfo().getPhone());
		dto.setModMesra(user.getInfo().getModMesra());

		return dto;
	}

}
