package com.roulette.resto.controller.roulette;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.roulette.in.NewSessionDto;
import com.roulette.resto.service.roulette.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")

public class ActivityController {
	final JwtService jwtService;
	final ActivityService activityService;
	public ActivityController(JwtService jwtService, ActivityService activityService) {
		this.jwtService = jwtService;
		this.activityService = activityService;
	}
	@Tag(name = "App | Activity")
	@DeleteMapping("/leave-activity/{activityId}")
	public ResponseEntity<?> removeAffectedActivity(Authentication authentication,@PathVariable	String activityId) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		activityService.removeAccountFromActivity(accountId,activityId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@GetMapping("/me")
	@Operation(summary = "Get my activities")
	public ResponseEntity<?> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityDto> activities = activityService.getActivityByAccountId(accountId);
		return new ResponseEntity<>(activities, HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@GetMapping("/{id}")
	@Operation(summary = "Get an activity")
	public ResponseEntity<?> getActivity(@PathVariable String id) {
		ActivityDto activity = activityService.getActivity(id);
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@PostMapping("/new-session")
	@Operation(summary = "Upload a new session")
	public ResponseEntity<?> uploadNewSession(Authentication authentication, @RequestBody NewSessionDto session) {
		int accountSessionHost = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityDto> activity = activityService.createNewSession(accountSessionHost, session);
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}

	@Tag(name = "App | Feed ")
	@GetMapping("/followers/feed")
	@Operation(summary = "Get my followers recents activities")
	public ResponseEntity<List<ActivityDto>> getFollowersFeed(Authentication authentication){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityDto> activities = activityService.getMyFollowersActivities(accountId);
		return new ResponseEntity<>(activities,HttpStatus.OK);
	}

}
