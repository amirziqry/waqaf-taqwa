package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NexGenCollectionResponse {

	private String code;

	private String name;

	private String description;

	private String status;

}
