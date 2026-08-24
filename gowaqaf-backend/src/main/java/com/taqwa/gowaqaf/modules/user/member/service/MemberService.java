package com.taqwa.gowaqaf.modules.user.member.service;

import java.util.List;

import com.taqwa.gowaqaf.modules.user.member.dto.MemberInfo;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterResponse;
import com.taqwa.gowaqaf.modules.user.member.dto.UpdateMemberRoleRequest;

public interface MemberService {

	MemberRegisterResponse createMemberEditor(MemberRegisterCredentials request);

	MemberRegisterResponse createMemberAdmin(MemberRegisterCredentials request);

	MemberInfo getMemberByUsername(String username);

	List<MemberInfo> getAllMembers();

	void updateMemberRole(String username, UpdateMemberRoleRequest dto);

	void deleteMemberByUsername(String username);

}
