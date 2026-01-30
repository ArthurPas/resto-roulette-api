package com.roulette.resto.repository.roulette;

import com.roulette.resto.dao.roulette.RouletteDao;
import com.roulette.resto.data.roulette.websocket.AccountChoices;
import com.roulette.resto.data.roulette.websocket.AccountsJoined;
import com.roulette.resto.data.roulette.websocket.RouletteSession;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RouletteRepository {

	final RouletteDao rouletteDao;

	public RouletteRepository(RouletteDao rouletteDao) {
		this.rouletteDao = rouletteDao;
	}

	public AccountsJoined addAccountIdToSession(String sessionId, int accountId) {
		rouletteDao.addAccountIdToSession(sessionId, accountId);
		RouletteSession rouletteSession = rouletteDao.getSession(sessionId);
		List<Integer> accountsId = rouletteSession.getAccountChoices().stream().map(AccountChoices::getAccountId).collect(Collectors.toList());
		AccountsJoined accountsJoined = new AccountsJoined();
		accountsJoined.setAccountsId(accountsId);
		return accountsJoined;
	}

	public RouletteSession createNewSession(String sessionId) {
		return rouletteDao.createSession(sessionId);
	}

	public String getSessionIdByShortId(String shortId) {
		return rouletteDao.getSessionIdByShortId(shortId);
	}

	public void addFoodChoice(String sessionId, AccountChoices choices) {
		rouletteDao.addFoodChoice(sessionId, choices);
	}
}
