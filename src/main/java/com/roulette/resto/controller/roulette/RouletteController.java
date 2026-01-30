package com.roulette.resto.controller.roulette;

import com.roulette.resto.data.roulette.in.NewSessionDto;
import com.roulette.resto.data.roulette.websocket.*;
import com.roulette.resto.service.roulette.RouletteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class RouletteController {

	final RouletteService rouletteService;

	public RouletteController(RouletteService rouletteService) {
		this.rouletteService = rouletteService;
	}

	@Tag(name = "App | Roulette")
	@PostMapping("/roulette/new-session")
	@Operation(summary = "create a new session")
	public ResponseEntity<RouletteSession> createNewSession() {
		RouletteSession rouletteSession = rouletteService.createNewSession();
		return new ResponseEntity<>(rouletteSession, HttpStatus.CREATED);
	}
	@Tag(name = "App | Roulette")
	@GetMapping("/roulette/session/{shortId}")
	@Operation(summary = "get sessionid by short id")
	public ResponseEntity<?> getSessionUuid(@PathVariable String shortId) {
		String uuid =  rouletteService.getSessionIdByShortId(shortId);
		record SessionIdResponse(String sessionId){};
		return new ResponseEntity<>(new SessionIdResponse(uuid), HttpStatus.OK);
	}

	@MessageMapping("/start/{sessionId}")
	@SendTo("/session/{sessionId}")
	public SessionStatus createNewSession(@DestinationVariable String sessionId) {
		return new SessionStatus(true, false, false);
	}

	@MessageMapping("/join/{sessionId}")
	@SendTo("/session/{sessionId}")
	public AccountsJoined joinSession(@DestinationVariable String sessionId, JoinSession account) throws Exception {
		return rouletteService.addAccountToCurrentSession(sessionId, account);
	}
	@MessageMapping("/swipe/{sessionId}")
	public void swipe(@DestinationVariable String sessionId, AccountChoices choices) throws Exception {
		rouletteService.addFoodChoice(sessionId, choices);
	}

}
