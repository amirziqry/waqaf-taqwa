package com.taqwa.gowaqaf.modules.auth.member.service;

import com.taqwa.gowaqaf.modules.auth.member.dto.MemberAuthDetails;
import com.taqwa.gowaqaf.modules.auth.member.dto.MemberLoginCredentials;

public interface MemberAuthService {

	MemberAuthDetails login(MemberLoginCredentials request);

}
