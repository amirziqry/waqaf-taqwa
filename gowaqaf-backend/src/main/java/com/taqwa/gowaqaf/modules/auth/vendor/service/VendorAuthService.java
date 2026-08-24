package com.taqwa.gowaqaf.modules.auth.vendor.service;

import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorLoginCredentials;

public interface VendorAuthService {

	VendorAuthDetails login(VendorLoginCredentials request);

}
