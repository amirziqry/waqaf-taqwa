package com.taqwa.gowaqaf.modules.dashboard.rakanqr.service;

import com.taqwa.gowaqaf.modules.dashboard.rakanqr.dto.RakanQrDashboard;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface RakanQrDashboardService {

	RakanQrDashboard getDashboardByUser(CustomUserDetails principal);

}
