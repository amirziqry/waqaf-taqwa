package com.taqwa.gowaqaf.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

	private UUID id;

	private String username;

	private String accountHolderName;

	private String email;

	private String phone;

	private String role;

	private Boolean modMesra;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime createdAt;

}
