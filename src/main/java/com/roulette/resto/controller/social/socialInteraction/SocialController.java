package com.roulette.resto.controller.social.socialInteraction;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.CommentResponse;
import com.roulette.resto.data.social.dto.out.SocialInteractionResponse;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.dto.MinimalAccountInfo;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.SocialService;
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

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/social")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class SocialController {

	final SocialService socialService;
	final UserService userService;
	final JwtService jwtService;
	private final AccountService accountService;

	public record SocialInteractions(List<SocialInteractionResponse> socialInteractionResponses) {}
	public record FollowRequests(List<MinimalAccountInfo> requests){}
	public record Followers(List<MinimalAccountInfo> followers){}

	public SocialController(SocialService socialService, JwtService jwtService, UserService userService, AccountService accountService) {
		this.socialService = socialService;
		this.jwtService = jwtService;
		this.userService = userService;
		this.accountService = accountService;
	}
	@Tag(name = "Social | Comments ")
	@PostMapping("/resto/{activity_id}/add-comment")
	@Operation(summary = "Add comment on an activity")
	public ResponseEntity<CommentResponse> newComment(Authentication authentication, @PathVariable String activity_id,
										@RequestBody @Valid NewComment newComment){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		int commentId = socialService.addComment(activity_id, accountId,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@PatchMapping("/edit-comment/{comment_id}")
	@Operation(summary = "Change comment")
	public ResponseEntity<CommentResponse> editComment(Authentication authentication, @PathVariable String comment_id,
										@RequestBody @Valid NewComment newComment){
		int commentId = socialService.editComment(comment_id,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@DeleteMapping("/delete-comment/{comment_id}")
	@Operation(summary = "Delete comment")
	public ResponseEntity<Void> deleteComment(Authentication authentication, @PathVariable String comment_id){
		socialService.deleteComment(comment_id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Social | Comments")
	@GetMapping("/me")
	@Operation(summary = "Get my social interactions (comments)")
	public ResponseEntity<SocialInteractions> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<SocialInteractionResponse> comment = socialService.getInteractionsByAccountId(accountId);
		return new ResponseEntity<>(new SocialInteractions(comment), HttpStatus.OK);
	}
	@Tag(name = "Account | Followers")
	@GetMapping("/following-request")
	@Operation(summary = "Get my following request that was not accepted yet")
	public ResponseEntity<FollowRequests> GetFollowingRequest(Authentication authentication){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		return new ResponseEntity<>(new FollowRequests(userService.getIngoingFollowingRequest(accountId)),HttpStatus.OK);
	}


	@Tag(name = "Account | Followers")
	@PostMapping("/ask-for-follow/{accountAskedId}")
	@Operation(summary = "Following request from account who proceed api call to accountAsked")
	public ResponseEntity<Void> follow(Authentication authentication, @PathVariable String accountAskedId){
		int accountAskerId = jwtService.getAccountIdAuthenticated(authentication);
		userService.askForFollow(accountAskerId,accountAskedId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Account | Followers")
	@PostMapping("/accept-follow/{accountAskerId}")
	@Operation(summary = "Accept following request from accountAskerId ")
	public ResponseEntity<Void> acceptFollow(Authentication authentication, @PathVariable String accountAskerId){
		int accountAskedId = jwtService.getAccountIdAuthenticated(authentication);
		userService.acceptFollow(accountAskedId,accountAskerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Account | Followers")
	@PostMapping("/unfollow/{accountIdToUnfollow}")
	@Operation(summary = "Unfollow accountIdToUnfollow")
	public ResponseEntity<Void> unfollow(Authentication authentication, @PathVariable String accountIdToUnfollow){
		int accountAsked = jwtService.getAccountIdAuthenticated(authentication);
		userService.unfollow(accountAsked,accountIdToUnfollow);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@Tag(name = "Account | Followers")
	@GetMapping("/followers")
	@Operation(summary = "Get my followers")
	public ResponseEntity<Followers> getFollowersList(Authentication authentication) throws AccountNotFoundException {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<MinimalAccountInfo> followers = userService.getFollowersByAccountId(accountId);
		return new ResponseEntity<>(new Followers(followers),HttpStatus.OK);
	}

	@Tag(name = "Account | profile")
	@GetMapping("/profile/{login}")
	@Operation(summary = "Get profile")
	public ResponseEntity<MinimalAccountInfo> getProfile(@PathVariable String login, Authentication authentication) throws AccountNotFoundException {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		Account connectedAccount = accountService.getAccountById(accountId);
		Account accountByLogin = accountService.getAccountByLogin(login);
		SocialService.FollowersStatus followersStatus = socialService.getFollowersStatus(connectedAccount,accountByLogin);
		log.warn(followersStatus.toString());
		return new ResponseEntity<>(new MinimalAccountInfo(accountByLogin, followersStatus),HttpStatus.OK);
	}

//	@Tag(name = "Social | Resto interactions")
//	@PostMapping("/resto/{restoId}/like")
//	@Operation(summary = "Like a resto", description = "Basic like interaction")
//	public ResponseEntity<LikedResto> likeResto(Authentication authentication,@PathVariable String restoId){
//		int accountId = jwtService.getAccountIdAuthenticated(authentication);
//		LikedResto likedResto = interactionsService.likeResto(restoId, accountId);
//		return new ResponseEntity<>(likedResto,HttpStatus.OK);
//	}
//	@Tag(name = "Social | Resto interactions")
//	@DeleteMapping("/resto/{restoId}/unlike")
//	@Operation(summary = "Remove like of a resto previously liked", description = "Basic dislike interaction")
//	public ResponseEntity<LikedResto> dislikeResto(Authentication authentication,@PathVariable String restoId){
//		int accountId = jwtService.getAccountIdAuthenticated(authentication);
//		LikedResto likedResto = interactionsService.dislikeResto(restoId, accountId);
//		return new ResponseEntity<>(likedResto,HttpStatus.OK);
//	}
}
