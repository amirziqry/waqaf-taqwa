package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexGenCreateCollectionRequest {
	
	private String fieldName;

	private String fieldDescription;

	private String fieldStatus;
}
