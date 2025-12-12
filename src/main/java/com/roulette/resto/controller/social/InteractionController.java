package com.roulette.resto.controller.social;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.service.social.InteractionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
