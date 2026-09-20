package com.taqwa.gowaqaf.modules.user.service.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.mapper.AccountMapper;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;
import com.taqwa.gowaqaf.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final AccountRepository repository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Register.
	 */
	@Override
	@Transactional
	public RegisterResponse createUser(RegisterCredentials dto) {
		Account user = new Account();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRoles(Set.of(Role.USER));

		// Set account details.
		user.setAccountHolderName(dto.getAccountHolderName());
		user.setPhone(dto.getPhone());
		user.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());

		Account saved = repository.save(user);

		return AccountMapper.mapToRegisterResponse(saved);
	}

	/**
	 * Update personal account details.
	 */
	@Transactional
	@Override
	public void updateUserInfoById(UUID userId, AccountUploadFields request) {
		Account user = getUserById(userId);

		user.setAccountHolderName(request.getAccountHolderName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setModMesra(request.getModMesra() == null ? false : request.getModMesra());

		repository.save(user);
	}

	@Override
	public Account getUserById(UUID userId) {
		Account user = repository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.USR001, String.format("User %s not found.", userId)));

		return user;
	}

	/**
	 * Get personal account info.
	 */
	@Override
	public AccountInfo getUserDetailsById(UUID userId) {
		Account user = getUserById(userId);

		return AccountMapper.mapToAccountInfo(user);
	}

	/**
	 * Change password.
	 */
	@Override
	public void changeUserPassword(UUID userId, ChangePasswordRequest request) {
		Account user = getUserById(userId);

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
			throw new BadCredentialsException("Current password is incorrect");

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
			throw new BadCredentialsException("New password must be different from current password");

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
	}

}
