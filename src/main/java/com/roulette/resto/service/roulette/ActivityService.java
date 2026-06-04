package com.roulette.resto.service.roulette;

import com.roulette.resto.dao.roulette.ActivityDao;
import com.roulette.resto.data.resto.dto.out.MinimalRestoInfo;
import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.ActivityResponse;
import com.roulette.resto.data.roulette.websocket.AccountsInSession;
import com.roulette.resto.data.social.dto.out.CommentInfo;
import com.roulette.resto.data.social.dto.out.CommentInfoWithAccount;
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
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
public class ActivityService {
	final ActivityRepository activityRepository;
	final AccountService accountService;
	final UserService userService;
	private final RouletteService rouletteService;
	private final RestoService restoService;

	public ActivityService(ActivityRepository activityRepository, AccountService accountService, UserService userService, RouletteService rouletteService, ActivityDao activityDao, RestoService restoService) {
		this.activityRepository = activityRepository;
		this.accountService = accountService;
		this.userService = userService;
		this.rouletteService = rouletteService;
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
		activityResponses.sort(Comparator.comparing(ActivityResponse::getActivityDate).reversed());
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
		ActivityDto activity = activityRepository.getActivityById(Integer.parseInt(activityId));
		List<CommentInfo> commentInfos = activityRepository.getCommentsByActivityId(Integer.parseInt(activityId));
		if(activity == null) {
			throw new APIError(144, HttpStatus.NOT_FOUND);
		}
		ActivityResponse activityResponse = buildActivityResponse(activity);
		List<CommentInfoWithAccount> commentInfosWithAccounts = new ArrayList<>();
		for (CommentInfo comment : commentInfos) {
			CommentInfoWithAccount commentWithInfo = new CommentInfoWithAccount(comment);
			commentWithInfo.setAuthor(new MinimalAccountInfo(accountService.getAccountById(comment.getAuthorId())));
			commentInfosWithAccounts.add(commentWithInfo);
		}
		activityResponse.setComments(commentInfosWithAccounts);
		activityResponse.setNbComments(commentInfos.size());
		return activityResponse;
	}


	public void saveSession(String sessionId, RestoDto resto) {
		AccountsInSession accountsInSession = rouletteService.getAccountsStatus(sessionId);
		List<Integer> accountsIds = new ArrayList<>();
		for (String login : accountsInSession.getAccountsJoined()) {
			accountsIds.add(accountService.getAccountByLogin(login).getAccountId());
		}

		ZonedDateTime nowInFrance = ZonedDateTime.now(ZoneId.of("Europe/Paris"));

		activityRepository.createActivities(
				accountsIds,
				defaultActivityDescription(resto.getName(), nowInFrance),
				sessionId,
				resto.getId()
		);
	}

	public List<ActivityDto> getActivitiesBySessionId(String sessionId) {
		return activityRepository.getActivitiesBySessionId(sessionId);
	}
	public static String defaultActivityDescription(String restoName, ZonedDateTime dateTime) {
		String day = dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE).toLowerCase();

		String formattedRestoName = "";
		if (restoName != null && !restoName.isEmpty()) {
			formattedRestoName = restoName.substring(0, 1).toUpperCase() + restoName.substring(1).toLowerCase();
		}

		String description = formattedRestoName + " le " + day;

		int hour = dateTime.getHour();
		int minute = dateTime.getMinute();

		if (hour < 15 || (hour == 15 && minute < 30)) {
			return description + " midi";
		} else if (hour < 18) {
			return description + " au gouter ";
		} else {
			return description + " soir";
		}
	}

	public int getNbActivityPendingByAccountId(int accountId) {
		return activityRepository.getNbActivityPendingByAccountId(accountId);
	}

	public ActivityResponse updateActivityDescription(String id, String description) {
		if(activityRepository.updateActivityDescription(Integer.parseInt(id), description) ==1){
			return buildActivityResponse(activityRepository.getActivityById(Integer.parseInt(id)));
		};
		throw new APIError(64, HttpStatus.NOT_FOUND);
	}

	public ActivityResponse updateActivityUploadStatus(String id, boolean upload) {
		if(activityRepository.updateActivityUploadStatus(Integer.parseInt(id), upload) == 1){
			return buildActivityResponse(activityRepository.getActivityById(Integer.parseInt(id)));
		}
		throw new APIError(64, HttpStatus.NOT_FOUND);
	}

	public List<ActivityResponse> getMyFollowersActivities(int accountId) throws AccountNotFoundException {
		List<MinimalAccountInfo> followers = userService.getFollowersByAccountId(accountId);
		List<Integer> followersIds = new ArrayList<>();
		for (MinimalAccountInfo follower : followers) {
			followersIds.add(follower.getAccountId());
		}

		List<ActivityDto> activities = activityRepository.getActivitiesByAccountIds(followersIds);
		List<ActivityResponse> responseList = new ArrayList<>();

		for (ActivityDto activity : activities) {
			if (!activity.isUploaded()) {
				continue;
			}
			responseList.add(this.getActivity(String.valueOf(activity.getActivityId())));
		}
		return responseList;
	}
}


