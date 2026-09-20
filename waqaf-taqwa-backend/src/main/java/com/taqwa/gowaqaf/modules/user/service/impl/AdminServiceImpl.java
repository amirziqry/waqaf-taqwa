package com.taqwa.gowaqaf.modules.user.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.dto.admin.UpdateAdminRoleRequest;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.mapper.AccountMapper;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;
import com.taqwa.gowaqaf.modules.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AccountRepository repository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public RegisterResponse createEditor(RegisterCredentials dto) {
		Account user = new Account();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRoles(Set.of(Role.EDITOR));

		// Set account details.
		user.setAccountHolderName(dto.getAccountHolderName());
		user.setPhone(dto.getPhone());
		user.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());

		Account saved = repository.save(user);

		return AccountMapper.mapToRegisterResponse(saved);
	}

	@Override
	@Transactional
	public RegisterResponse createAdmin(RegisterCredentials dto) {
		Account user = new Account();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRoles(Set.of(Role.ADMIN));

		// Set account details.
		user.setAccountHolderName(dto.getAccountHolderName());
		user.setPhone(dto.getPhone());
		user.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());

		Account saved = repository.save(user);

		return AccountMapper.mapToRegisterResponse(saved);
	}

	@Override
	public List<AccountInfo> getAllAdmins() {
		List<Account> users = repository.findByRolesIn(List.of(Role.SUPER_ADMIN, Role.ADMIN, Role.EDITOR));

		List<AccountInfo> dtos = users.stream().map(member -> AccountMapper.mapToAccountInfo(member)).toList();

		return dtos;
	}

	@Override
	public void updateAdminRoleById(UUID userId, UpdateAdminRoleRequest dto) {
		Account user = repository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.USR001, String.format("Admin %s not found", userId)));

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
	public void deleteAdminById(UUID userId) {
		if (!repository.existsById(userId))
			throw new ResourceNotFoundException(ErrorCode.USR001, String.format("Admin %s not found", userId));

		repository.deleteById(userId);
	}

}
