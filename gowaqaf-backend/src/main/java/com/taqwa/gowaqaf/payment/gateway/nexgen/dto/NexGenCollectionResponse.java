package com.taqwa.gowaqaf.payment.gateway.nexgen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenCollectionResponse {

	private String code;
	private String name;
	private String description;
	private String status;

}
