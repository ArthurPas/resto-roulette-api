package com.roulette.resto.service.roulette;

import com.roulette.resto.data.roulette.Activity;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.repository.roulette.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityService {
	final ActivityRepository activityRepository;

	public ActivityService(ActivityRepository activityRepository) {
		this.activityRepository = activityRepository;
	}

	public void removeAccountFromActivity(int accountId, String activityId) {
		int nbActivityDeleted = activityRepository.removeAccountFromActivity(accountId,activityId);
		if (nbActivityDeleted > 0) {
			log.warn("No activities found for account " + accountId + " and activity " + activityId);
		}
	}

	public List<ActivityDto> getMyActivities(int accountId) {
		return activityRepository.getMyActivities(accountId);
	}
}


