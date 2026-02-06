package com.roulette.resto.service.roulette;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.roulette.websocket.AccountChoices;
import com.roulette.resto.data.roulette.websocket.AccountsJoined;
import com.roulette.resto.data.roulette.websocket.JoinSession;
import com.roulette.resto.data.roulette.websocket.RouletteSession;
import com.roulette.resto.repository.roulette.RouletteRepository;
import com.roulette.resto.service.resto.RestoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RouletteService {

	final RouletteRepository rouletteRepository;
	final RestoService restoService;

	public RouletteService(RouletteRepository rouletteRepository, RestoService restoService) {
		this.rouletteRepository = rouletteRepository;
		this.restoService = restoService;
	}

	public AccountsJoined addAccountToCurrentSession(String sessionId, JoinSession account) {
		return rouletteRepository.addAccountIdToSession(sessionId, account.getAccountId());

	}

	public RouletteSession createNewSession() {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		return rouletteRepository.createNewSession(sessionId);
	}

	public String getSessionIdByShortId(String shortId) {
		return rouletteRepository.getSessionIdByShortId(shortId);
	}

	public void addFoodChoice(String sessionId, AccountChoices choices) {
		rouletteRepository.addFoodChoice(sessionId, choices);
	}

	public List<RestoDto> getMatchedRestosBySessionId(String sessionId) {
		List<AccountChoices> choices = rouletteRepository.getChoiceBySession(sessionId);
		if (choices.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> foodTypesCommon = new HashSet<>(choices.get(0).getFoodLiked());
		for (AccountChoices choice : choices) {
			foodTypesCommon.retainAll(choice.getFoodLiked());
		}

		log.info("Found {} common food types for this session", foodTypesCommon.size());
		if (foodTypesCommon.isEmpty()) {
			return Collections.emptyList();
		}

		return restoService.getRestosByTypes(foodTypesCommon);
	}
}
