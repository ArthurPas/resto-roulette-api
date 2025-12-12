package com.roulette.resto.controller.common;

import com.roulette.resto.service.common.MediaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/medias")
@CrossOrigin(origins = "*") //TODO: add strict origin
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@Tag(name = "Medias")
public class MediaController {
	final MediaService mediaService;

	public MediaController(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<Void> getSinglePicture(@PathVariable String uuid) {
		log.info("Getting picture for {}", uuid);
		return ResponseEntity.ok()
		.header("X-Accel-Redirect", "/stockage_interne/" + uuid)
		.cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
		.build();
	}

}
