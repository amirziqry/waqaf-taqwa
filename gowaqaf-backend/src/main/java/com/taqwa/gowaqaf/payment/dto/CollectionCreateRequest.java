package com.taqwa.gowaqaf.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionCreateRequest {
	
	private String name;
	private String description;
	private CollectionStatus status;

}
