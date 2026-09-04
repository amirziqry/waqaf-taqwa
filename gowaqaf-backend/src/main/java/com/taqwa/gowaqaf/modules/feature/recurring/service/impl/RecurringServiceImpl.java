package com.taqwa.gowaqaf.modules.feature.recurring.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.feature.recurring.dto.RecurringApplicationRequest;
import com.taqwa.gowaqaf.modules.feature.recurring.entity.Recurring;
import com.taqwa.gowaqaf.modules.feature.recurring.enums.FrequencyType;
import com.taqwa.gowaqaf.modules.feature.recurring.enums.RecurringType;
import com.taqwa.gowaqaf.modules.feature.recurring.repository.RecurringRepository;
import com.taqwa.gowaqaf.modules.feature.recurring.service.RecurringService;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurringServiceImpl implements RecurringService {

	private final RecurringRepository recurringRepository;
	private final PersonalService personalService;
	private final ProjectService projectService;

	@Override
	public void createRecurringByUser(AccountUserDetails principal, RecurringApplicationRequest request) {
		Personal personal = personalService.getPersonalByUsername(principal.getUsername());

		Recurring recurring = new Recurring();

		recurring.setPersonal(personal);

		if (request.getType() == RecurringType.PROJECT)
			recurring.setProject(projectService.getProjectById(request.getProjectId()));

		recurring.setAmount(request.getAmount());

		recurring.setType(request.getType());
		recurring.setFrequency(request.getFrequency());
		recurring.setAutoRoundUp(request.getAutoRoundUp());
		recurring.setNextPaymentAt(calculateNextPayment(request.getFrequency()));

		recurringRepository.save(recurring);
	}

	private LocalDateTime calculateNextPayment(FrequencyType frequency) {

		LocalDateTime now = LocalDateTime.now();

		return switch (frequency) {

		case FrequencyType.SUBUH -> {
			LocalDateTime next = now.toLocalDate().atTime(6, 0);

			if (!next.isAfter(now))
				next = next.plusDays(1);

			yield next;
		}

		case FrequencyType.JUMAAT -> {
			LocalDate today = now.toLocalDate();

			LocalDate nextFriday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

			LocalDateTime next = nextFriday.atTime(13, 0);

			if (!next.isAfter(now))
				next = nextFriday.plusWeeks(1).atTime(13, 0);

			yield next;
		}

		case FrequencyType.BULANAN -> {
			LocalDateTime next = now.with(TemporalAdjusters.firstDayOfNextMonth()).toLocalDate().atStartOfDay();

			yield next;
		}
		};
	}

	@Override
	public void processDuePayments() {
		System.out.println("Recurring scheduler triggered");
	}

}
