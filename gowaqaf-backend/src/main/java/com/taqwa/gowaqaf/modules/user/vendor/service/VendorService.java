package com.taqwa.gowaqaf.modules.user.vendor.service;

import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterResponse;

public interface VendorService {

	VendorRegisterResponse createDonator(VendorRegisterCredentials request);

}
