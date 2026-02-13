package com.roulette.resto.repository.roulette;

import com.roulette.resto.dao.roulette.RouletteDao;
import com.roulette.resto.data.roulette.websocket.AccountChoices;
import com.roulette.resto.data.roulette.websocket.AccountsInSession;
import com.roulette.resto.data.roulette.websocket.RouletteSession;
import com.roulette.resto.data.roulette.websocket.VetoResto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class RouletteRepository {

	final RouletteDao rouletteDao;

	public RouletteRepository(RouletteDao rouletteDao) {
		this.rouletteDao = rouletteDao;
	}

	public AccountsInSession addAccountIdToSession(String sessionId, int accountId) {
		rouletteDao.addAccountIdToSession(sessionId, accountId);
		RouletteSession rouletteSession = rouletteDao.getSession(sessionId);
		List<Integer> accountsId = rouletteSession.getAccountChoices().stream().map(AccountChoices::getAccountId).collect(Collectors.toList());
		AccountsInSession accountsJoined = new AccountsInSession();
		accountsJoined.setAccountsJoinedIds(accountsId);
		log.info(accountsJoined.toString());
		return accountsJoined;
	}

	public RouletteSession createNewSession(String sessionId) {
		return rouletteDao.createSession(sessionId);
	}

	public String getSessionIdByShortId(String shortId) {
		return rouletteDao.getSessionIdByShortId(shortId);
	}
	public RouletteSession getSessionById(String sessionId) {
		return rouletteDao.getSession(sessionId);
	}


	public void addFoodChoices(String sessionId, AccountChoices choices) {
		rouletteDao.addFoodChoices(sessionId, choices);
	}

	public List<AccountChoices> getChoiceBySession(String sessionId) {
		return rouletteDao.getChoicesBySession(sessionId);
	}

	public void saveMatchingRestos(Set<Integer> restoIds, String sessionId) {
		rouletteDao.saveMatchingRestos(restoIds,sessionId);
	}

	public Set<Integer> removeRestos(String sessionId, VetoResto vetoResto) {
		Set<Integer> remainingsRestos = rouletteDao.removeRestoFromSession(sessionId, vetoResto);
		log.info(remainingsRestos.toString());
		rouletteDao.setVetoStatusForAccount(sessionId,vetoResto.getAccountId(),true);
		return remainingsRestos;
	}

	public Set<Integer> getRestosBySession(String sessionId) {
		return rouletteDao.getRestosBySession(sessionId);
	}
}
