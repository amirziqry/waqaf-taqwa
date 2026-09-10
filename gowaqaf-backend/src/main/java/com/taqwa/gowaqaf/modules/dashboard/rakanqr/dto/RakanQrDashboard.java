package com.taqwa.gowaqaf.modules.dashboard.rakanqr.dto;

import java.util.List;

import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrDashboard {

	private RakanQrInfo info;

	private RakanQrCollection collectedAmount;

	private List<RakanQrDonationDetails> donations;

}
