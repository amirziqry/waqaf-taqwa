package com.taqwa.gowaqaf.modules.feature.recurring.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taqwa.gowaqaf.modules.feature.recurring.service.RecurringService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecurringScheduler {

	private final RecurringService recurringService;

	@Scheduled(fixedRate = 10000)
	public void processRecurringPayments() {
		recurringService.processDuePayments();
	}

}
