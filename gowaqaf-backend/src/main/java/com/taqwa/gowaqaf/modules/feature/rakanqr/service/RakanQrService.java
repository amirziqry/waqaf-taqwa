package com.taqwa.gowaqaf.modules.feature.rakanqr.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSumFilter;

public interface RakanQrService {

	List<RakanQrInfo> getAllRakanQr(RakanQrFilter filter);

	RakanQrInfo createRakanQr(Authentication authentication);

	void updateRakanQrStatus(UUID id, RakanQrStatusRequest request);

	List<RakanQrWithSum> getAllRakanQrWithSum(RakanQrWithSumFilter filter);

	List<RakanQrWithSum> getAllRakanQrWithSum(LocalDate startDate, LocalDate endDate);

}
