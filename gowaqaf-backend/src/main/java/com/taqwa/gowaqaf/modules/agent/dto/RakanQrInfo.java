package com.taqwa.gowaqaf.modules.agent.dto;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RakanQrInfo {

	private UUID id;

	private String code;

	private AgentType type;

	private AgentStatus status;

	private String email;

}
