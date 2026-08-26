package com.taqwa.gowaqaf.modules.agent.mapper;

import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;

public class RakanQrMapper {

	public static RakanQrInfo mapToInfo(RakanQr model) {

		RakanQrInfo dto = new RakanQrInfo();

		dto.setId(model.getId());
		dto.setCode(model.getCode());
		dto.setType(model.getType());
		dto.setStatus(model.getStatus());

		if (model.getType() == AgentType.MERCHANT)
			dto.setEmail(model.getMerchant().getEmail());

		if (model.getType() == AgentType.PERSONAL)
			dto.setEmail(model.getPersonal().getEmail());

		return dto;
	}

}
