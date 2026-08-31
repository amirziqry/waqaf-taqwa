package com.taqwa.gowaqaf.modules.rakanqr.mapper;

import com.taqwa.gowaqaf.modules.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.rakanqr.entity.RakanQr;

public class RakanQrMapper {

	public static RakanQrInfo mapToInfo(RakanQr model) {

		RakanQrInfo dto = new RakanQrInfo();

		dto.setId(model.getId());
		dto.setCode(model.getCode());
		dto.setType(model.getType());
		dto.setStatus(model.getStatus());

		if (model.getType() == RakanQrType.MERCHANT)
			dto.setEmail(model.getMerchant().getInfo().getEmail());

		if (model.getType() == RakanQrType.PERSONAL)
			dto.setEmail(model.getPersonal().getInfo().getEmail());

		return dto;
	}

}
