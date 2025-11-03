package com.roulette.resto.resto.controllers;

import com.roulette.resto.administration.dto.out.AccountsInfos;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.ErrorResponse;
import com.roulette.resto.resto.dto.in.NewRestaurant;
import com.roulette.resto.resto.services.RestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resto")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class RestoController {

	final private RestoService restoService;

	public RestoController(RestoService restoService) {
		this.restoService = restoService;
	}

	@PostMapping("/create")
	@Operation(summary = "Create a restaurant")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Accounts info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AccountsInfos.class)))})
	public ResponseEntity<?> create(Authentication authentication, @RequestBody NewRestaurant newRestaurant) {
		try {
			return new ResponseEntity<>(restoService.createResto(newRestaurant),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
}
