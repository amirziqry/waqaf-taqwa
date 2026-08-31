package com.taqwa.gowaqaf.modules.organization.content.project.dto;

import java.math.BigDecimal;

import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWithCollection {

	private Project project;

	private BigDecimal collectedAmount;

}
