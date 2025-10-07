package com.roulette.resto.business.administration.controllers;

import com.roulette.resto.business.administration.dto.out.TrendDto;
import com.roulette.resto.business.administration.services.KpiService;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserRole;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@RestController
@RequestMapping("/kpi")
@CrossOrigin(origins = "*")
@SecurityRequirement(name="Bearer Authentication")
public class KpiController {

	final KpiService kpiService;
	private final JwtService jwtService;
	private final AccountService accountService;

	public KpiController(KpiService kpiService, JwtService jwtService, AccountService accountService) {
		this.kpiService = kpiService;
		this.jwtService = jwtService;
		this.accountService = accountService;
	}

	@GetMapping("/yearlyRegistrations")
	@Operation(summary = "New accounts account for a year", description = "Return an array of 12 integer " +
			"which represent the amount of created accounts by month (first index for january, second for february .." +
			".) for the year given")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total by month 12 length array",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =String.class, example = "[\n" +
									"  2,\n" +
									"  3,\n" +
									"  3,\n" +
									"  4,\n" +
									"  5,\n" +
									"  5,\n" +
									"  6,\n" +
									"  4,\n" +
									"  9,\n" +
									"  10,\n" +
									"  0,\n" +
									"  0\n" +
									"]")))})
	public ResponseEntity<?> getUsersRegistrationsByYear(@RequestParam String year, Authentication authentication) throws AccountNotFoundException {
		final ResponseEntity<?> UNAUTHORIZED = rightCheck(authentication);
		if(UNAUTHORIZED != null) return UNAUTHORIZED;
		long[] history = kpiService.getUsersRegistrationHistoric(year);
		return new ResponseEntity<>(history, HttpStatus.OK);
	}

	@GetMapping("/registrationTrend")
	@Operation(summary = "Registration trend between current month and last month", description = "Return the total " +
			"number of created account and the trend between current and last month in percentage.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total and trend data",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =TrendDto.class)))})
	public ResponseEntity<?> getTotalUsersRegistrations(Authentication authentication) {
		final ResponseEntity<?> UNAUTHORIZED = rightCheck(authentication);
		if(UNAUTHORIZED != null) return UNAUTHORIZED;
		TrendDto trendDto = kpiService.getRegistrationTrend();
		return new ResponseEntity<>(trendDto, HttpStatus.OK);
	}


	@GetMapping("/launchedWheels")
	public ResponseEntity<?> getLaunchedWheels(Authentication authentication) {
		final ResponseEntity<?> UNAUTHORIZED = rightCheck(authentication);
		if(UNAUTHORIZED != null) return UNAUTHORIZED;
		TrendDto trendDto = kpiService.getWheelTrend();
		return new ResponseEntity<>(trendDto, HttpStatus.OK);

	}


	private ResponseEntity<?> rightCheck(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		log.warn(String.valueOf(accountId));
		try {
			Account account = accountService.getAccountById(accountId);
			if(account.getUserInfo().getRole()!= UserRole.ROLE_ADMIN){
				return new ResponseEntity<>(new APIError("You are not allowed to see this resource, only admin " +
						"profile can"),
						HttpStatus.UNAUTHORIZED);
			}
		}catch (AccountNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return null;
	}
}
