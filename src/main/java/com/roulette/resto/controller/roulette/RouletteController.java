package com.roulette.resto.controller.roulette;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.roulette.websocket.*;
import com.roulette.resto.data.social.dto.out.SessionIdResponse;
import com.roulette.resto.data.social.entity.Resto;
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

import java.util.ArrayList;
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
		log.info(rouletteSession.toString());
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
	public BroadcastSessionResponse createNewSession(@DestinationVariable String sessionId) {
		log.info(sessionId);
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		response.setStatus(SessionStatus.SWIPE);
		AccountsInSession accountsInSession = rouletteService.getAccountsStatus(sessionId);
		response.setAccountsInSession(accountsInSession.getAccountsJoined());
		response.setAccountsRemaining(accountsInSession.getAccountsJoined());
		log.info("send : {}",response);
		return response;
	}

	@MessageMapping("/join/{sessionId}")
	@SendTo("/session/{sessionId}")
	public BroadcastSessionResponse joinSession(@DestinationVariable String sessionId, JoinSession account)  {
		log.info(sessionId + "Account : " + account.getLogin());
		AccountsInSession accountsInSession = rouletteService.addAccountToCurrentSession(sessionId, account);
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		response.setStatus(SessionStatus.LOBBY);
		response.setAccountsInSession(accountsInSession.getAccountsJoined());
		log.info("send : {}",response);
		return response;
	}
	@MessageMapping("/swipe/{sessionId}")
	@SendTo("/session/{sessionId}")
	public BroadcastSessionResponse swipe(@DestinationVariable String sessionId, AccountChoices choices) {
		rouletteService.addFoodChoices(sessionId, choices);
		log.info(sessionId + "choices : " + choices.toString());
		AccountsInSession accountsInSession = rouletteService.getAccountsStatus(sessionId);
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		response.setStatus(SessionStatus.SWIPE);
		List<String> accountInSession = accountsInSession.getAccountsJoined();
		response.setAccountsInSession(accountInSession);
		accountInSession.removeAll(accountsInSession.getAccountsSwiped());
		response.setAccountsRemaining(accountInSession);
		log.info("send : {}",response);
		return response;
	}
	@MessageMapping("/swipe-done/{sessionId}")
	@SendTo("/session/{sessionId}")
	public BroadcastSessionResponse sendResto(@DestinationVariable String sessionId) {
		List<RestoDto> restos = rouletteService.getMatchedRestosBySessionId(sessionId);
		rouletteService.saveMatchingRestos(restos,sessionId);
		log.info("{} restos : {}", sessionId, restos);
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		response.setStatus(SessionStatus.VETO);
		response.setRestoCandidates(restos);
		AccountsInSession accountsInSession = rouletteService.getAccountsStatus(sessionId);
		response.setAccountsInSession(accountsInSession.getAccountsJoined());
		response.setAccountsRemaining(accountsInSession.getAccountsJoined());
		log.info("send : {}",response);
		return response;
	}
	@MessageMapping("/veto/{sessionId}")
	@SendTo("/session/{sessionId}")
	public BroadcastSessionResponse addVeto(@DestinationVariable String sessionId, VetoResto veto) {
		rouletteService.removeResto(sessionId, veto);
		log.info("{} veto : {}", sessionId, veto);
		AccountsInSession status = rouletteService.getAccountsStatus(sessionId);
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		response.setStatus(SessionStatus.VETO);
		List<String> totalJoined = new ArrayList<>(status.getAccountsJoined());
		response.setAccountsInSession(totalJoined);
		List<String> remaining = new ArrayList<>(totalJoined);
		remaining.removeAll(status.getAccountsVeto());
		response.setAccountsRemaining(remaining);
		response.setRestoCandidates(rouletteService.getRestoBySession(sessionId));
		log.info("Broadcast response: {}", response);
		return response;
	}
	@MessageMapping("/veto-done/{sessionId}")
	@SendTo("/session/{sessionId}")
	public BroadcastSessionResponse onVetoDone(@DestinationVariable String sessionId) {
		BroadcastSessionResponse response = new BroadcastSessionResponse();
		RestoDto winner = rouletteService.randomWinnerResto(sessionId);
		response.setWinner(winner);
		response.setStatus(SessionStatus.RESULT);
		log.info("send : {}",response);
		return response;
	}

}
