package com.taqwa.gowaqaf.modules.user.admin.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminInfo;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.admin.dto.AdminRegisterResponse;
import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.admin.dto.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.mapper.AdminMapper;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
import com.taqwa.gowaqaf.modules.user.admin.service.AdminService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository repository;
	private final AccountInfoRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public AdminRegisterResponse createEditor(AdminRegisterCredentials dto) {
		Admin user = new Admin();
		AccountInfo info = new AccountInfo();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRoles(Set.of(Role.EDITOR));

		// Set account details.
		info.setEmail(dto.getEmail());
		info.setPhone(dto.getPhone());
		info.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());
		user.setInfo(accountRepository.save(info));

		Admin saved = repository.save(user);

		return AdminMapper.mapToRegisterResponse(saved);
	}

	@Override
	@Transactional
	public AdminRegisterResponse createAdmin(AdminRegisterCredentials dto) {
		Admin user = new Admin();
		AccountInfo info = new AccountInfo();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRoles(Set.of(Role.ADMIN));

		// Set account details.
		info.setEmail(dto.getEmail());
		info.setPhone(dto.getPhone());
		info.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());
		user.setInfo(accountRepository.save(info));

		Admin saved = repository.save(user);

		return AdminMapper.mapToRegisterResponse(saved);
	}

	@Override
	public AdminInfo getAdminByUsername(String username) {
		Admin user = repository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username)));

		return AdminMapper.mapToAdminInfo(user);
	}

	@Override
	public List<AdminInfo> getAllAdmins() {
		List<Admin> users = repository.findAll();

		List<AdminInfo> dtos = users.stream().map(member -> AdminMapper.mapToAdminInfo(member)).toList();

		return dtos;
	}

	@Override
	public void updateAdminRole(String username, UpdateAdminRoleRequest dto) {
		Admin user = repository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username)));

		Role role;

		try {
			role = Role.valueOf(dto.role().toUpperCase());

		} catch (IllegalArgumentException | NullPointerException e) {
			throw new BadRequestException(ErrorCode.ROL001,
					String.format("Invalid role %s not found", dto.role().toUpperCase()));
		}

		user.setRoles(new HashSet<>(Set.of(role)));

		repository.save(user);
	}

	@Override
	public void changePasswordByUsername(AccountUserDetails principal, ChangePasswordRequest request) {
		Admin user = repository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MBR001,
						String.format("User %s not found", principal.getUsername())));

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
			throw new BadCredentialsException("Current password is incorrect");

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
			throw new IllegalArgumentException("New password must be different from current password");

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
	}

	@Override
	public void deleteAdminByUsername(String username) {
		if (!repository.existsByUsername(username))
			throw new ResourceNotFoundException(ErrorCode.MBR001, String.format("Member %s not found", username));

		repository.deleteByUsername(username);

		return;
	}

}
