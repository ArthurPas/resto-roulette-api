package com.roulette.resto.service.roulette;

import com.roulette.resto.dao.roulette.ActivityDao;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.RouletteSessionDto;
import com.roulette.resto.data.roulette.in.NewSessionDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.roulette.ActivityRepository;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ActivityService {
	final ActivityRepository activityRepository;
	final AccountService accountService;
	final UserService userService;
	private final ActivityDao activityDao;

	public ActivityService(ActivityRepository activityRepository, AccountService accountService, UserService userService, ActivityDao activityDao) {
		this.activityRepository = activityRepository;
		this.accountService = accountService;
		this.userService = userService;
		this.activityDao = activityDao;
	}

	public void removeAccountFromActivity(int accountId, String activityId) {
		int nbActivityDeleted = activityRepository.removeAccountFromActivity(accountId,activityId);
		if (nbActivityDeleted == 0) {
			log.warn("No activity found for account " + accountId + " and activity " + activityId);
			throw new APIError(144, HttpStatus.NOT_FOUND);
		}
	}

	public List<ActivityDto> getActivityByAccountId(int accountId) {

		List<ActivityDto> activities =  activityRepository.getActivityByAccountId(accountId);
		for (ActivityDto activity : activities) {
			try {
				List<MinimalAccountInfo> participantsInfo = new ArrayList<>();
				List<Account> accounts = accountService.getAccountsByIds(activity.getDetails().participantIds);
				accounts.forEach(account -> {participantsInfo.add(new MinimalAccountInfo(account));});
				activity.getDetails().setParticipantInfos(participantsInfo);
			}catch (AccountNotFoundException e) {
				log.error("account not found " + accountId);
				throw new APIError(64, HttpStatus.NOT_FOUND);
			}

		}
		return activities;
	}

	public ActivityDto getActivity(String activityId) {
		ActivityDto activity = activityDao.getActivityById(Integer.parseInt(activityId));
		if(activity == null) {
			throw new APIError(144, HttpStatus.NOT_FOUND);
		}
		return activity;
	}

	public List<ActivityDto> getMyFollowersActivities(int accountId) {
		List<MinimalAccountInfo> followers = userService.getFollowersByAccountId(accountId);
		List<ActivityDto> activities = new ArrayList<>();
		for (MinimalAccountInfo follower: followers) {
			List<ActivityDto> followerActivities = getActivityByAccountId(follower.getAccountId());
			activities.addAll(followerActivities);
		}
		return activities;
	}

	public List<ActivityDto> createNewSession(int accountSessionHost, NewSessionDto session) {
		String sessionId = activityDao.createActivity(accountSessionHost,session.getDescription(),
				session.getRestoId());
		for (int accountId : session.getParticipantsIds()){
			if(accountId!=accountSessionHost){
				activityDao.createActivity(accountId,session.getDescription(),session.getRestoId(), sessionId);
			}
		}
		return getActivitiesBySessionId(sessionId);
	}

	public List<ActivityDto> getActivitiesBySessionId(String sessionId) {
		return activityDao.getActivitiesBySessionId(sessionId);
	}
}


