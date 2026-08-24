package com.taqwa.gowaqaf.modules.organization.project.dto;

import java.util.List;
import java.util.UUID;

import com.taqwa.gowaqaf.storage.dto.UploadUrl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUploadResponse {

	private UUID id;
	private List<UploadUrl> uploadUrl;

}
