package com.roulette.resto.controller.social.interaction;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
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
@Tag(name = "Account | Social interactions")
public class InteractionController {

	final InteractionsService interactionsService;
	final UserService userService;
	final JwtService jwtService;

	public InteractionController(InteractionsService interactionsService, JwtService jwtService, UserService userService, UserService userService1) {
		this.interactionsService = interactionsService;
		this.jwtService = jwtService;
		this.userService = userService1;
	}
	@Tag(name = "Account | Resto interactions")
	@PostMapping("/resto/{restoId}/like")
	@Operation(summary = "Like a resto", description = "Basic like interaction")
	public ResponseEntity<?> likeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.likeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}
	@Tag(name = "Account | Resto interactions")
	@DeleteMapping("/resto/{restoId}/unlike")
	@Operation(summary = "Remove like of a resto previously liked", description = "Basic dislike interaction")
	public ResponseEntity<?> dislikeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.dislikeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}
	@Tag(name = "Account | Social interactions")
	@PostMapping("/resto/{activity_id}/add-comment")
	@Operation(summary = "Add comment on an activity")
	public ResponseEntity<?> newComment(Authentication authentication, @PathVariable String activity_id,
										@RequestBody @Valid NewComment newComment){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.addComment(activity_id, accountId,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Account | Social interactions")
	@PatchMapping("/edit-comment/{comment_id}")
	@Operation(summary = "Change comment")
	public ResponseEntity<?> editComment(Authentication authentication, @PathVariable String comment_id,
										@RequestBody @Valid NewComment newComment){
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.editComment(comment_id,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}

	@Tag(name = "Account | Social interactions")
	@DeleteMapping("/delete-comment/{comment_id}")
	@Operation(summary = "Delete comment")
	public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable String comment_id){
		interactionsService.deleteComment(comment_id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Tag(name = "Account | Social interactions")
	@GetMapping("/me")
	@Operation(summary = "Get my social interactions (comments)")
	public ResponseEntity<?> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<SocialInteraction> comment = interactionsService.getInteractionsByAccountId(accountId);
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}

	@Tag(name = "Account | Users interactions")
	@PostMapping("/ask-for-follow/{accountAskedId}")
	@Operation(summary = "Following request from account who proceed api call to accountAsked")
	public ResponseEntity<?> follow(Authentication authentication, @PathVariable String accountAskedId){
		int accountAsker = jwtService.getAccountIdAuthenticated(authentication);
		userService.askForFollow(accountAsker,accountAskedId);
		return new ResponseEntity<>(HttpStatus.OK);
	}



}
