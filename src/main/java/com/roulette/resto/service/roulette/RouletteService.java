package com.roulette.resto.service.roulette;

import com.roulette.resto.data.roulette.websocket.AccountsJoined;
import com.roulette.resto.data.roulette.websocket.JoinSession;
import com.roulette.resto.data.roulette.websocket.RouletteSession;
import com.roulette.resto.repository.roulette.RouletteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RouletteService {

	final RouletteRepository rouletteRepository;

	public RouletteService(RouletteRepository rouletteRepository) {
		this.rouletteRepository = rouletteRepository;
	}

	public AccountsJoined addAccountToCurrentSession(String sessionId, JoinSession account) {
		return rouletteRepository.addAccountIdToSession(sessionId, account.getAccountId());

	}

	public RouletteSession createNewSession() {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		return rouletteRepository.createNewSession(sessionId);
	}
}
