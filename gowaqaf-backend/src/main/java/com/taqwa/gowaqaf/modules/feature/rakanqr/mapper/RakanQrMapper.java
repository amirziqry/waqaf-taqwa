package com.taqwa.gowaqaf.modules.feature.rakanqr.mapper;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrAccount;

public class RakanQrMapper {

	public static RakanQrInfo mapToInfo(RakanQr rakanqr) {
		RakanQrInfo dto = new RakanQrInfo();

		dto.setId(rakanqr.getId());
		dto.setCode(rakanqr.getCode());
		dto.setType(rakanqr.getType());
		dto.setStatus(rakanqr.getStatus());
		dto.setCollectedAmount(rakanqr.getCollectedAmount());
		dto.setCommission(rakanqr.getCommission());

		if (rakanqr.getAccount() == RakanQrAccount.MERCHANT) {
			dto.setName(rakanqr.getMerchant().getInfo().getAccountHolderName());
			dto.setEmail(rakanqr.getMerchant().getInfo().getEmail());
			dto.setPhone(rakanqr.getMerchant().getInfo().getPhone());
		}

		if (rakanqr.getAccount() == RakanQrAccount.PERSONAL) {
			dto.setName(rakanqr.getPersonal().getInfo().getAccountHolderName());
			dto.setEmail(rakanqr.getPersonal().getInfo().getEmail());
			dto.setPhone(rakanqr.getPersonal().getInfo().getPhone());
		}

		return dto;
	}

}
