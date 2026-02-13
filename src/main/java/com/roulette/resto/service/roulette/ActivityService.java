package com.roulette.resto.service.roulette;

import com.roulette.resto.dao.roulette.ActivityDao;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.resto.dto.out.MinimalRestoInfo;
import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.ActivityResponse;
import com.roulette.resto.data.roulette.in.NewSessionDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.roulette.ActivityRepository;
import com.roulette.resto.service.resto.RestoService;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ActivityService {
	final ActivityRepository activityRepository;
	final AccountService accountService;
	final UserService userService;
	private final ActivityDao activityDao;
	private final RestoService restoService;

	public ActivityService(ActivityRepository activityRepository, AccountService accountService, UserService userService, ActivityDao activityDao, RestoService restoService) {
		this.activityRepository = activityRepository;
		this.accountService = accountService;
		this.userService = userService;
		this.activityDao = activityDao;
		this.restoService = restoService;
	}

	public void removeAccountFromActivity(int accountId, String activityId) {
		int nbActivityDeleted = activityRepository.removeAccountFromActivity(accountId,activityId);
		if (nbActivityDeleted == 0) {
			log.warn("No activity found for account " + accountId + " and activity " + activityId);
			throw new APIError(144, HttpStatus.NOT_FOUND);
		}
	}

	public List<ActivityResponse> getActivitiesByAccountId(int accountId) {
		List<ActivityResponse> activityResponses = new ArrayList<>();

		List<ActivityDto> activities =  activityRepository.getActivityByAccountId(accountId);
		for (ActivityDto activity : activities) {
			ActivityResponse activityResponse = buildActivityResponse(activity);
			activityResponses.add(activityResponse);
		}
		return activityResponses;
	}

	private ActivityResponse buildActivityResponse(ActivityDto activity) {
		try {

			ActivityResponse activityResponse = new ActivityResponse(activity);

			List<MinimalAccountInfo> participantsInfo = getMinimalAccountInfos(activity);
			activityResponse.setParticipantInfos(participantsInfo);

			MinimalRestoInfo restoInfo = restoService.getMinimalRestoInfo(String.valueOf(activity.getDetails().getRestoId()));
			activityResponse.setRestoInfo(restoInfo);
			return activityResponse;
		}catch (AccountNotFoundException e) {
			log.error("account not found " + activity.getAccountId());
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	private List<MinimalAccountInfo> getMinimalAccountInfos(ActivityDto activity) throws AccountNotFoundException {
		List<MinimalAccountInfo> participantsInfo = new ArrayList<>();
		List<Account> accounts = accountService.getAccountsByIds(activity.getDetails().participantIds);
		accounts.forEach(account -> {participantsInfo.add(new MinimalAccountInfo(account));});
		return participantsInfo;
	}



	public ActivityResponse getActivity(String activityId) {
		ActivityDto activity = activityDao.getActivityById(Integer.parseInt(activityId));

		if(activity == null) {
			throw new APIError(144, HttpStatus.NOT_FOUND);
		}
		return buildActivityResponse(activity);
	}

	public List<ActivityResponse> getMyFollowersActivities(int accountId) {
		List<MinimalAccountInfo> followers = userService.getFollowersByAccountId(accountId);
		List<ActivityResponse> activities = new ArrayList<>();
		for (MinimalAccountInfo follower: followers) {
			List<ActivityResponse> followerActivities = getActivitiesByAccountId(follower.getAccountId());
			activities.addAll(followerActivities);
		}
		return activities;
	}

	public List<ActivityResponse> createNewSession(int accountSessionHost, NewSessionDto session) throws AccountNotFoundException {
		String sessionId = activityDao.createActivity(accountSessionHost,session.getDescription(),
				session.getRestoId());
		for (int accountId : session.getParticipantsIds()){
			if(accountId!=accountSessionHost){
				activityDao.createActivity(accountId,session.getDescription(),session.getRestoId(), sessionId);
			}
		}
		List<ActivityDto> activities = getActivitiesBySessionId(sessionId);
		List<ActivityResponse> activitiesResponse = new ArrayList<>();
		for (ActivityDto activity : activities) {
			activitiesResponse.add(buildActivityResponse(activity));
		}
		return activitiesResponse;
	}

	public List<ActivityDto> getActivitiesBySessionId(String sessionId) {
		return activityDao.getActivitiesBySessionId(sessionId);
	}
}


