package com.roulette.resto.business.administration.controllers;

import com.roulette.resto.business.administration.dto.UserRegistrationHistory;
import com.roulette.resto.business.administration.services.KpiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kpi")
@CrossOrigin(origins = "*")
public class KpiController {

	final KpiService kpiService;

	public KpiController(KpiService kpiService) {
		this.kpiService = kpiService;
	}

	@GetMapping("/newUsers")
	public ResponseEntity<?> getUsersRegistrationsByYear(@RequestParam String year) {
		long[] history = kpiService.getUsersRegistration(year);
		return new ResponseEntity<>(history, HttpStatus.OK);
	}

}
