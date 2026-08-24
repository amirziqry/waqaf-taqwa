package com.taqwa.gowaqaf.modules.user.member.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberInfo;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.member.dto.MemberRegisterResponse;
import com.taqwa.gowaqaf.modules.user.member.dto.UpdateMemberRoleRequest;
import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.entity.Role;
import com.taqwa.gowaqaf.modules.user.member.mapper.MemberMapper;
import com.taqwa.gowaqaf.modules.user.member.repository.MemberRepository;
import com.taqwa.gowaqaf.modules.user.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public MemberRegisterResponse createMemberEditor(MemberRegisterCredentials request) {
		Member user = new Member();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(Set.of(Role.EDITOR));

		Member saved = memberRepository.save(user);

		return MemberMapper.mapToRegisterResponse(saved);
	}

	@Override
	public MemberRegisterResponse createMemberAdmin(MemberRegisterCredentials request) {
		Member user = new Member();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(Set.of(Role.EDITOR, Role.ADMIN));

		Member saved = memberRepository.save(user);

		return MemberMapper.mapToRegisterResponse(saved);
	}

	@Override
	public MemberInfo getMemberByUsername(String username) {
		Member user = memberRepository.findByUsername(username);
		if (user == null)
			throw new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username));

		return MemberMapper.mapToMemberInfo(user);
	}

	@Override
	public List<MemberInfo> getAllMembers() {
		List<Member> members = memberRepository.findAll();

		List<MemberInfo> dtos = members.stream().map(member -> MemberMapper.mapToMemberInfo(member)).toList();

		return dtos;
	}

	@Override
	public void updateMemberRole(String username, UpdateMemberRoleRequest dto) {
		Member member = memberRepository.findByUsername(username);
		if (member == null)
			throw new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username));

		Role role;

		try {
			role = Role.valueOf(dto.role().toUpperCase());

		} catch (IllegalArgumentException | NullPointerException e) {
			throw new BadRequestException(ErrorCode.ROL001,
					String.format("Invalid role %s not found", dto.role().toUpperCase()));
		}

		member.setRoles(new HashSet<>(Set.of(role)));

		memberRepository.save(member);
	}

	@Override
	public void deleteMemberByUsername(String username) {
		if (!memberRepository.existsByUsername(username))
			throw new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username));

		memberRepository.deleteByUsername(username);

		return;
	}

}
