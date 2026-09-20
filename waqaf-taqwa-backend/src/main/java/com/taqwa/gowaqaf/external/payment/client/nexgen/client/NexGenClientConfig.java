package com.taqwa.gowaqaf.external.payment.client.nexgen.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NexGenClientConfig {

	@Value("${nexgen.base-url}")
	String baseUrl;

	@Value("${nexgen.api.key}")
	String apiKey;

	@Bean
	RestClient nexGenRestClient() {

		return RestClient.builder().baseUrl(baseUrl).defaultHeader("ApiKey", apiKey).build();
	}

}
