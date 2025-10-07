package com.roulette.resto.business.administration.controllers;

import com.roulette.resto.business.administration.dto.out.GlobalUserStatDto;
import com.roulette.resto.business.administration.services.KpiService;
import com.roulette.resto.business.social.dto.out.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/kpi")
@CrossOrigin(origins = "*")
@SecurityRequirement(name="Bearer Authentication")
public class KpiController {

	final KpiService kpiService;

	public KpiController(KpiService kpiService) {
		this.kpiService = kpiService;
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
	public ResponseEntity<?> getUsersRegistrationsByYear(@RequestParam String year) {
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
							schema = @Schema(implementation =String.class, example = "{" +
									"  \"totalRegistered\": 54,\n" +
									"  \"variation\": 10.00,\n" +
									"  \"variationType\": \"UP (or DOWN or EQUAL)\"\n" +
									"}")))})
	public ResponseEntity<?> getTotalUsersRegistrations() {
		GlobalUserStatDto globalUserStatDto = kpiService.getVariationRegistration();
		return new ResponseEntity<>(globalUserStatDto, HttpStatus.OK);
	}

}
