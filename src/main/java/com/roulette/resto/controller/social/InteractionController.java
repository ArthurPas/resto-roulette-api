package com.roulette.resto.controller.social;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.service.social.InteractionsService;
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
	final JwtService jwtService;

	public InteractionController(InteractionsService interactionsService, JwtService jwtService) {
		this.interactionsService = interactionsService;
		this.jwtService = jwtService;
	}

	@PostMapping("/resto/{restoId}/like")
	@Operation(summary = "Like a resto", description = "Basic like interaction")
	public ResponseEntity<?> likeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.likeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}
	@DeleteMapping("/resto/{restoId}/unlike")
	@Operation(summary = "dislike a resto", description = "Basic dislike interaction")
	public ResponseEntity<?> dislikeResto(Authentication authentication,@PathVariable String restoId){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		LikedResto likedResto = interactionsService.dislikeResto(restoId, accountId);
		return new ResponseEntity<>(likedResto,HttpStatus.OK);
	}

	@PostMapping("/resto/{restoId}/add-comment")
	@Operation(summary = "Add comment on resto")
	public ResponseEntity<?> newComment(Authentication authentication, @PathVariable String restoId,
										@RequestBody @Valid NewComment newComment){
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.addComment(restoId, accountId,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}
	@PatchMapping("/edit-comment/{comment_id}")
	@Operation(summary = "Change previous comment")
	public ResponseEntity<?> editComment(Authentication authentication, @PathVariable String comment_id,
										@RequestBody @Valid NewComment newComment){
		record CommentResponse(String comment, int id){};
		int commentId = interactionsService.editComment(comment_id,newComment);
		return new ResponseEntity<>(new CommentResponse(newComment.getComment(), commentId),HttpStatus.OK);
	}
	@DeleteMapping("/delete-comment/{comment_id}")
	public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable String comment_id){
		interactionsService.deleteComment(comment_id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/me")
	@Operation(summary = "Get my interactions")
	public ResponseEntity<?> myInteractions(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<SocialInteraction> comment = interactionsService.getInteractionsByAccountId(accountId);
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}
}
