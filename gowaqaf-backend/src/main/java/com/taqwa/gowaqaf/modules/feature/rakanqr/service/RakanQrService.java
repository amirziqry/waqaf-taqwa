package com.taqwa.gowaqaf.modules.feature.rakanqr.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrFilter;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithCollection;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface RakanQrService {

	void updateRakanQrStatus(UUID id, RakanQrStatusRequest request);

	RakanQr getRakanQrByUser(AccountUserDetails principal);

	RakanQrInfo getRakanQrInfoByUser(AccountUserDetails principal);

	RakanQr getRakanQrByUser(String agentCode);

	Page<RakanQrInfo> getAllRakanQrInfo(Pageable pageable, RakanQrFilter filter);

	Page<RakanQrWithCollection> getAllRakanQrWithCollection(Pageable pageable, LocalDate startDate, LocalDate endDate);

	List<RakanQrInfo> getRakanQrInfoList(Pageable pageable);

	RakanQrInfo createRakanQr(AccountUserDetails principal, RakanQrApplicationRequest request);

	void updateRakanQrCollectedAmountById(UUID id, BigDecimal amount);

	RakanQr getRakanQrById(UUID rakanQrId);

}
