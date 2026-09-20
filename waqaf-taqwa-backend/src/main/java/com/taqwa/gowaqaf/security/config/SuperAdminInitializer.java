package com.taqwa.gowaqaf.security.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.super-admin.username}")
	private String username;

	@Value("${app.super-admin.password}")
	private String password;

	@Override
	public void run(ApplicationArguments args) {

		if (accountRepository.existsByUsername(username))
			return;

		Account account = new Account();
		account.setUsername(username);
		account.setEmail("admin@gmail.com");
		account.setPassword(passwordEncoder.encode(password));
		account.setRoles(Set.of(Role.SUPER_ADMIN));

		accountRepository.save(account);
	}

}
