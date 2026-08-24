package com.taqwa.gowaqaf.modules.user.member.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.taqwa.gowaqaf.modules.user.member.dto.MemberInfo;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterResponse;
import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.entity.Role;

public class MemberMapper {

	public static MemberRegisterResponse mapToRegisterResponse(Member member) {
		Set<String> roles = member.getRoles().stream().map(Role::name).collect(Collectors.toSet());

		return new MemberRegisterResponse(member.getUsername(), member.getEmail(), roles);
	}

	public static MemberInfo mapToMemberInfo(Member member) {

		return new MemberInfo(member.getId(), member.getUsername(), member.getEmail(), member.getRoles());
	}

}
