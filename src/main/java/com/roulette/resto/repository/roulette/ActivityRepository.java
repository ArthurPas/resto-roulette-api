package com.roulette.resto.repository.roulette;

import com.roulette.resto.dao.roulette.ActivityDao;
import com.roulette.resto.data.roulette.Activity;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ActivityRepository {
	final ActivityDao activityDao;

	public ActivityRepository(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	public int removeAccountFromActivity(int accountId, String activityId) {
		return activityDao.removeAccountFromActivity(accountId,Integer.parseInt(activityId));
	}

	public List<ActivityDto> getMyActivities(int accountId) {
		return activityDao.getActivitiesByAccountId(accountId);
	}
}
