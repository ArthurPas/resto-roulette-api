package com.roulette.resto.service.roulette;

import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.repository.roulette.ActivityRepository;
import com.roulette.resto.service.social.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ActivityService {
	final ActivityRepository activityRepository;
	private final AccountService accountService;

	public ActivityService(ActivityRepository activityRepository, AccountService accountService) {
		this.activityRepository = activityRepository;
		this.accountService = accountService;
	}

	public void removeAccountFromActivity(int accountId, String activityId) {
		int nbActivityDeleted = activityRepository.removeAccountFromActivity(accountId,activityId);
		if (nbActivityDeleted > 0) {
			log.warn("No activities found for account " + accountId + " and activity " + activityId);
		}
	}

	public List<ActivityDto> getMyActivities(int accountId) {

		List<ActivityDto> activities =  activityRepository.getMyActivities(accountId);
		for (ActivityDto activity : activities) {
			try {
				List<MinimalAccountInfo> participantsInfo = new ArrayList<>();
				List<Account> accounts = accountService.getAccountsByIds(activity.getDetails().participantIds);
				accounts.forEach(account -> {participantsInfo.add(new MinimalAccountInfo(account));});
				activity.getDetails().setParticipantInfos(participantsInfo);
			}catch (AccountNotFoundException e) {
				log.error("account not found " + accountId);
			}

		}
		return activities;
	}
}


