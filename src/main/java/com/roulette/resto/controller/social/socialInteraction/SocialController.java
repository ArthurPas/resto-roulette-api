package com.roulette.resto.controller.social.socialInteraction;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.data.social.entity.Activity;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.service.roulette.ActivityService;
import com.roulette.resto.service.social.InteractionsService;
import com.roulette.resto.service.social.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/social")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class SocialController {

	final InteractionsService interactionsService;
	final UserService userService;
	final JwtService jwtService;
	private final ActivityService activityService;

	public SocialController(InteractionsService interactionsService, JwtService jwtService, UserService userService, UserService userService1, ActivityService activityService) {
		this.interactionsService = interactionsService;
		this.jwtService = jwtService;
		this.userService = userService1;
		this.activityService = activityService;
	}
	@Tag(name = "Social | Resto interactions")
	@PostMapping("/resto/{restoId}/like")
	@Operation(summary = "Like a resto", description = "Basic like interaction")
	public ResponseEntity<?> likeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.likeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}
	@Tag(name = "Social | Resto interactions")
	@DeleteMapping("/resto/{restoId}/unlike")
	@Operation(summary = "Remove like of a resto previously liked", description = "Basic dislike interaction")
	public ResponseEntity<?> dislikeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.dislikeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}
	@Tag(name = "Social | Comments ")
	@PostMapping("/resto/{activity_id}/add-comment")
	@Operation(summary = "Add comment on an activity")
	public ResponseEntity<?> newComment(Authentication authentication, @PathVariable String activity_id,
										@RequestBody @Valid NewComment newComment){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.addComment(activity_id, accountId,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@PatchMapping("/edit-comment/{comment_id}")
	@Operation(summary = "Change comment")
	public ResponseEntity<?> editComment(Authentication authentication, @PathVariable String comment_id,
										@RequestBody @Valid NewComment newComment){
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.editComment(comment_id,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@DeleteMapping("/delete-comment/{comment_id}")
	@Operation(summary = "Delete comment")
	public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable String comment_id){
		interactionsService.deleteComment(comment_id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@GetMapping("/me")
	@Operation(summary = "Get my social interactions (comments)")
	public ResponseEntity<?> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<SocialInteraction> comment = interactionsService.getInteractionsByAccountId(accountId);
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}

	@Tag(name = "Account | Followers")
	@PostMapping("/ask-for-follow/{accountAskedId}")
	@Operation(summary = "Following request from account who proceed api call to accountAsked")
	public ResponseEntity<?> follow(Authentication authentication, @PathVariable String accountAskedId){
		int accountAskerId = jwtService.getAccountIdAuthenticated(authentication);
		userService.askForFollow(accountAskerId,accountAskedId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Account | Followers")
	@PostMapping("/accept-follow/{accountAskerId}")
	@Operation(summary = "Accept following request from accountAskerId ")
	public ResponseEntity<?> acceptFollow(Authentication authentication, @PathVariable String accountAskerId){
		int accountAskedId = jwtService.getAccountIdAuthenticated(authentication);
		userService.acceptFollow(accountAskedId,accountAskerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Account | Followers")
	@PostMapping("/unfollow/{accountToUnfollow}")
	@Operation(summary = "Unfollow accountToUnfollow")
	public ResponseEntity<?> unfollow(Authentication authentication, @PathVariable String accountToUnfollow){
		int accountAsked = jwtService.getAccountIdAuthenticated(authentication);
		userService.unfollow(accountAsked,accountToUnfollow);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@Tag(name = "Account | Followers")
	@GetMapping("/followers")
	@Operation(summary = "Get my followers")
	public ResponseEntity<?> getFollowersList(Authentication authentication){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<MinimalAccountInfo> followers = userService.getFollowersByAccountId(accountId);
		return new ResponseEntity<>(followers,HttpStatus.OK);
	}
	@Tag(name = "Account | Followers")
	@GetMapping("/followers/feed")
	@Operation(summary = "Get my followers recents activities")
	public ResponseEntity<?> getFollowersFeed(Authentication authentication){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<ActivityDto> activities = activityService.getMyFollowersActivities(accountId);
		return new ResponseEntity<>(activities,HttpStatus.OK);
	}

}
