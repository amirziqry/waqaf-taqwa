package com.taqwa.gowaqaf.modules.dashboard.rakanqr.service;

import com.taqwa.gowaqaf.modules.dashboard.rakanqr.dto.RakanQrDashboard;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface RakanQrDashboardService {

	RakanQrDashboard getDashboardByUser(AccountUserDetails principal);

}
