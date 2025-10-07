package com.roulette.resto.business.administration.controllers;

import com.roulette.resto.business.administration.dto.out.GlobalUserStatDto;
import com.roulette.resto.business.administration.services.KpiService;
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

	@GetMapping("/newUsers")
	public ResponseEntity<?> getUsersRegistrationsByYear(@RequestParam String year) {
		long[] history = kpiService.getUsersRegistrationHistoric(year);
		return new ResponseEntity<>(history, HttpStatus.OK);
	}

	@GetMapping("/newUsersTrend")
	public ResponseEntity<?> getTotalUsersRegistrations() {
		GlobalUserStatDto globalUserStatDto = kpiService.getVariationRegistration();
		return new ResponseEntity<>(globalUserStatDto, HttpStatus.OK);
	}

}
