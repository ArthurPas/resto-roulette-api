package com.roulette.resto.controller.administration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roulette.resto.data.administration.dto.out.TrendDto;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.service.administration.AdminService;
import com.roulette.resto.service.administration.KpiService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kpi")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class KpiController {

	final KpiService kpiService;
	private final AdminService adminService;

	public KpiController(KpiService kpiService, AdminService adminService) {
		this.kpiService = kpiService;
		this.adminService = adminService;
	}

	@GetMapping("/yearlyRegistrations")
	@Operation(summary = "New accounts account for a year", description = "Return an array of 12 integer " +
			"which represent the amount of created accounts by month (first index for january, second for february .." +
			".) for the year given")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total by month 12 length array",
					content = @Content(mediaType = "application/text",
							schema = @Schema(implementation = String.class, example = "[\n" +
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
	public ResponseEntity<?> getUsersRegistrationsByYear(@RequestParam String year, Authentication authentication) {
			adminService.rightCheckIsAdmin(authentication);
			List<Long> results = kpiService.getUsersRegistrationHistoric(year);
			Gson gson= new GsonBuilder().create();
			return new ResponseEntity<>(gson.toJson(results), HttpStatus.OK);
	}

	@GetMapping("/registrationTrend")
	@Operation(summary = "Registration trend between current month and last month", description = "Return the total " +
			"number of created account and the trend between current and last month in percentage.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total and trend data",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = TrendDto.class)))})
	public ResponseEntity<?> getTotalUsersRegistrations(Authentication authentication) {
		adminService.rightCheckIsAdmin(authentication);
		TrendDto trendDto = kpiService.getRegistrationTrend();
		return new ResponseEntity<>(trendDto, HttpStatus.OK);
	}


	@GetMapping("/launchedWheelsTrend")
	@Operation(summary = "Wheels launched trend", description = "Return the total " +
			"number of wheel launched and the trend between current and last month in percentage.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total and trend data",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = TrendDto.class)))})
	public ResponseEntity<?> getLaunchedWheels(Authentication authentication) {
		adminService.rightCheckIsAdmin(authentication);
		TrendDto trendDto = kpiService.getWheelTrend();
		return new ResponseEntity<>(trendDto, HttpStatus.OK);
	}
}
