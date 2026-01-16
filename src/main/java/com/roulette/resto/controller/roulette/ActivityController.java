package com.roulette.resto.controller.roulette;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.service.roulette.ActivityService;
import com.roulette.resto.service.social.InteractionsService;
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
		List<ActivityDto> activities = activityService.getMyActivities(accountId);
		return new ResponseEntity<>(activities, HttpStatus.OK);
	}
	@Tag(name = "App | Activity")
	@GetMapping("/{id}")
	@Operation(summary = "Get my activities")
	public ResponseEntity<?> getActivity(@PathVariable String id) {
		ActivityDto comment = activityService.getActivity(id);
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}

}
