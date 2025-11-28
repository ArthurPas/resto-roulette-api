package com.roulette.resto.common.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "*") //TODO: add strict origin
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class MediaController {
	@GetMapping("/{uuid}")
	public ResponseEntity<Void> getSinglePicture(@PathVariable String uuid) {
		log.info("Getting picture for {}", uuid);
		return ResponseEntity.ok()
		.header("X-Accel-Redirect", "/stockage_interne/" + uuid)
		.cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
		.build();
	}
}
