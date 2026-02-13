package com.roulette.resto.controller.roulette;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.roulette.websocket.*;
import com.roulette.resto.data.social.dto.out.SessionIdResponse;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class RouletteController {

	final RouletteService rouletteService;
	private final SimpMessagingTemplate messagingTemplate;
	public RouletteController(RouletteService rouletteService, SimpMessagingTemplate messagingTemplate) {
		this.rouletteService = rouletteService;
		this.messagingTemplate = messagingTemplate;
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
	public ResponseEntity<SessionIdResponse> getSessionUuid(@PathVariable String shortId) {
		String uuid =  rouletteService.getSessionIdByShortId(shortId);
		return new ResponseEntity<>(new SessionIdResponse(uuid), HttpStatus.OK);
	}

	@MessageMapping("/start/{sessionId}")
	@SendTo("/session/{sessionId}")
	public SessionStatus createNewSession(@DestinationVariable String sessionId) {
		return new SessionStatus(true, false, false);
	}

	@MessageMapping("/join/{sessionId}")
	@SendTo("/session/{sessionId}")
	public AccountsInSession joinSession(@DestinationVariable String sessionId, JoinSession account)  {
		return rouletteService.addAccountToCurrentSession(sessionId, account);
	}
	@MessageMapping("/swipe/{sessionId}")
	@SendTo("/session/{sessionId}")
	public AccountsInSession swipe(@DestinationVariable String sessionId, AccountChoices choices) {
		rouletteService.addFoodChoices(sessionId, choices);
		return rouletteService.getAccountsStatus(sessionId);
	}
	@MessageMapping("/swipe-done/{sessionId}")
	@SendTo("/session/{sessionId}")
	public List<RestoDto> sendResto(@DestinationVariable String sessionId) {
		List<RestoDto> restos = rouletteService.getMatchedRestosBySessionId(sessionId);
		rouletteService.saveMatchingRestos(restos,sessionId);
		messagingTemplate.convertAndSend("/session/" + sessionId, new SessionStatus(true, true, false));
		return restos;
	}
	@MessageMapping("/veto/{sessionId}")
	@SendTo("/session/{sessionId}")
	public AccountsInSession addVeto(@DestinationVariable String sessionId, VetoResto veto) {
		rouletteService.removeResto(sessionId, veto);
		return rouletteService.getAccountsStatus(sessionId);
	}
	@MessageMapping("/veto-done/{sessionId}")
	@SendTo("/session/{sessionId}")
	public RestoDto onVetoDone(@DestinationVariable String sessionId) {
		List<RestoDto> remainingRestos = rouletteService.getRestoBySession(sessionId);
		messagingTemplate.convertAndSend("/session/" + sessionId, new SessionStatus(true, true, true));
		return rouletteService.randomWinnerResto(remainingRestos);
	}

}
