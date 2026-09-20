package com.taqwa.gowaqaf.modules.dashboard.personal.service;

import com.taqwa.gowaqaf.modules.dashboard.personal.dto.PersonalDashboard;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface PersonalDashboardService {

	PersonalDashboard getDashboardByUser(CustomUserDetails principal);

}
