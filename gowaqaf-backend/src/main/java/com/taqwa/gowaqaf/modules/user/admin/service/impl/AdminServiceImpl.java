package com.taqwa.gowaqaf.modules.user.admin.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.dto.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.entity.Role;
import com.taqwa.gowaqaf.modules.user.admin.mapper.AdminMapper;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
import com.taqwa.gowaqaf.modules.user.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public AdminRegisterResponse createEditor(AdminRegisterCredentials request) {
		Admin user = new Admin();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(Set.of(Role.EDITOR));

		Admin saved = adminRepository.save(user);

		return AdminMapper.mapToRegisterResponse(saved);
	}

	@Override
	public AdminRegisterResponse createAdmin(AdminRegisterCredentials request) {
		Admin user = new Admin();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(Set.of(Role.EDITOR, Role.ADMIN));

		Admin saved = adminRepository.save(user);

		return AdminMapper.mapToRegisterResponse(saved);
	}

	@Override
	public AdminInfo getAdminByUsername(String username) {
		Admin user = adminRepository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username)));

		return AdminMapper.mapToAdminInfo(user);
	}

	@Override
	public List<AdminInfo> getAllAdmins() {
		List<Admin> admins = adminRepository.findAll();

		List<AdminInfo> dtos = admins.stream().map(member -> AdminMapper.mapToAdminInfo(member)).toList();

		return dtos;
	}

	@Override
	public void updateAdminRole(String username, UpdateAdminRoleRequest dto) {
		Admin user = adminRepository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username)));

		Role role;

		try {
			role = Role.valueOf(dto.role().toUpperCase());

		} catch (IllegalArgumentException | NullPointerException e) {
			throw new BadRequestException(ErrorCode.ROL001,
					String.format("Invalid role %s not found", dto.role().toUpperCase()));
		}

		user.setRoles(new HashSet<>(Set.of(role)));

		adminRepository.save(user);
	}

	@Override
	public void deleteAdminByUsername(String username) {
		if (!adminRepository.existsByUsername(username))
			throw new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username));

		adminRepository.deleteByUsername(username);

		return;
	}

}
