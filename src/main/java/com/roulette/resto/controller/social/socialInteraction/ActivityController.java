package com.roulette.resto.controller.social.socialInteraction;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.configuration.TrackCampaign;
import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.ActivityResponse;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.service.resto.RestoService;
import com.roulette.resto.service.roulette.ActivityService;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")

public class ActivityController {
	final JwtService jwtService;
	final ActivityService activityService;

	public record ActivitiesResponse(List<ActivityResponse> activities) {}
	public record ActivityDescription(String description) {}
	public record UploadPayload(boolean uploaded) {}
	public ActivityController(JwtService jwtService, ActivityService activityService) {
		this.jwtService = jwtService;
		this.activityService = activityService;
	}
	@Tag(name = "App | Activity")
	@DeleteMapping("/leave-activity/{activityId}")
	public ResponseEntity<Void> removeAffectedActivity(Authentication authentication,@PathVariable	String activityId) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		activityService.removeAccountFromActivity(accountId,activityId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@GetMapping("/me")
	@Operation(summary = "Get my activities")
	public ResponseEntity<ActivitiesResponse> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityResponse> activities = activityService.getActivitiesByAccountId(accountId);
		return new ResponseEntity<>(new ActivitiesResponse(activities), HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@GetMapping("/{id}")
	@Operation(summary = "Get an activity")
	public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id) {
		ActivityResponse activity = activityService.getActivity(id);
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@PatchMapping("/update-description/{id}")
	@Operation(summary = "Update activity description")
	public ResponseEntity<ActivityResponse> updateActivityDescription(@PathVariable String id, @RequestBody ActivityDescription description) {
		ActivityResponse activity = activityService.updateActivityDescription(id, description.description);
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@PatchMapping("/update-status/{id}")
	@Operation(summary = "Update activity upload status")
	public ResponseEntity<ActivityResponse> changeUploadStatus(@PathVariable String id,
															   @RequestBody UploadPayload uploadPayload) {
		log.warn("###############################"+uploadPayload.toString());
		ActivityResponse activity = activityService.updateActivityUploadStatus(id, uploadPayload.uploaded);
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}


//	@Tag(name = "App | Activity")
//	@PostMapping("/new-session")
//	@Operation(summary = "Upload a new session")
//	public ResponseEntity<List<ActivityDto>> uploadNewSession(Authentication authentication, @RequestBody NewSessionDto session) {
//		int accountSessionHost = jwtService.getAccountIdAuthenticated(authentication);
//		List<ActivityDto> activity = activityService.createNewSession(accountSessionHost, session);
//		return new ResponseEntity<>(activity, HttpStatus.OK);
//	}

	@Tag(name = "App | Feed ")
	@GetMapping("/followers/feed")
	@TrackCampaign(type = "Feed")
	@Operation(summary = "Get my followers recents activities")
	public ResponseEntity<ActivitiesResponse> getFollowersFeed(Authentication authentication) throws AccountNotFoundException {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityResponse> activities = activityService.getMyFollowersActivities(accountId);

		return new ResponseEntity<>(new ActivitiesResponse(activities),HttpStatus.OK);
	}

}
