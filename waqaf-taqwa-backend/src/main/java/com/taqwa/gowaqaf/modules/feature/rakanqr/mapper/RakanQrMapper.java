package com.taqwa.gowaqaf.modules.feature.rakanqr.mapper;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;

public class RakanQrMapper {

	public static RakanQrInfo mapToInfo(RakanQr rakanqr) {
		RakanQrInfo dto = new RakanQrInfo();

		dto.setId(rakanqr.getId());
		dto.setCode(rakanqr.getCode());
		dto.setType(rakanqr.getType());
		dto.setStatus(rakanqr.getStatus());

		dto.setFullName(rakanqr.getFullName());
		dto.setIcNumber(rakanqr.getIcNumber());
		dto.setPhone(rakanqr.getPhone());

		dto.setRepresentativeType(rakanqr.getRepresentativeType());
		dto.setEstablishmentName(rakanqr.getEstablishmentName());
		dto.setQrSpot(rakanqr.getQrSpot());
		dto.setPostalAddress(rakanqr.getPostalAddress());
		dto.setBankName(rakanqr.getBankName());
		dto.setBankAccountNumber(rakanqr.getBankAccountNumber());

		dto.setCollectionCode(rakanqr.getCollectionCode());
		dto.setCollectedAmount(rakanqr.getCollectedAmount());
		dto.setCommission(rakanqr.getCommission());

		dto.setCreatedAt(rakanqr.getCreatedAt());

		return dto;
	}

}
