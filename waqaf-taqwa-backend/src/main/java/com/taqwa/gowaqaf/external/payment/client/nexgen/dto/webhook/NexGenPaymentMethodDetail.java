package com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NexGenPaymentMethodDetail {

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime transactionDate;

	private String transactionId;

	private String orderId;

}
