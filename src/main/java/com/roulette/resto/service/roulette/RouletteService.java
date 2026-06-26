package com.roulette.resto.service.roulette;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.roulette.websocket.*;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.roulette.RouletteRepository;
import com.roulette.resto.service.resto.RestoService;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouletteService {

	final RouletteRepository rouletteRepository;
	final RestoService restoService;

	public RouletteService(RouletteRepository rouletteRepository, RestoService restoService){
		this.rouletteRepository = rouletteRepository;
		this.restoService = restoService;
	}

	public AccountsInSession addAccountToCurrentSession(String sessionId, JoinSession account)  {
		return rouletteRepository.addAccountIdToSession(sessionId, account.getLogin());

	}

	public RouletteSession createNewSession() {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		return rouletteRepository.createNewSession(sessionId);
	}

	public String getSessionIdByShortId(String shortId) {
		String sessionId = rouletteRepository.getSessionIdByShortId(shortId);
		if(sessionId == null) {
			throw new APIError(34, HttpStatus.NOT_FOUND);
		}
		return sessionId;
	}

	public void addFoodChoices(String sessionId, AccountChoices choices) {
		rouletteRepository.addFoodChoices(sessionId, choices);
	}

	public AccountsInSession getAccountsStatus(String sessionId) {
		RouletteSession rouletteSession = rouletteRepository.getSessionById(sessionId);

		Set<String> accountsId = new HashSet<>();
		Set<String> accountsIdSwiped = new HashSet<>();
		Set<String> accountsIdVeto = new HashSet<>();

		for (AccountChoices ac : rouletteSession.getAccountChoices()) {
			String login = ac.getLogin();
			accountsId.add(login);

			if (!ac.getFoodLiked().isEmpty() || !ac.getFoodDisliked().isEmpty()) {
				accountsIdSwiped.add(login);
			}

			if (ac.isVetoDone()) {
				accountsIdVeto.add(login);
			}
		}
		AccountsInSession accountsInSession = new AccountsInSession();
		accountsInSession.setAccountsJoined(accountsId);
		accountsInSession.setAccountsSwiped(accountsIdSwiped);
		accountsInSession.setAccountsVeto(accountsIdVeto);
//		log.info("{} veto : {}", sessionId, accountsInSession);
		return accountsInSession;
	}
	public Set<RestoDto> getMatchedRestosBySessionId(String sessionId) {
		List<AccountChoices> choices = rouletteRepository.getChoiceBySession(sessionId);
		if (choices.isEmpty()) {
			return Collections.emptySet();
		}
		Set<String> foodTypesCommon = new HashSet<>(choices.getFirst().getFoodLiked());
		for (AccountChoices choice : choices) {
			foodTypesCommon.retainAll(choice.getFoodLiked());
		}

		log.info("Found {} common food types for this session", foodTypesCommon.size());
		if (foodTypesCommon.isEmpty()) {
			return Collections.emptySet();
		}

		return restoService.getRestosByTypes(foodTypesCommon);
	}

	public void saveMatchingRestos(Set<RestoDto> restoDtos, String sessionId) {
		Set<Integer> restoIds = new HashSet<>();
		for (RestoDto restoDto : restoDtos) {
			restoIds.add(restoDto.getId());
		}
		rouletteRepository.saveMatchingRestos(restoIds,sessionId);
	}

	public Set<RestoDto> removeResto(String sessionId, VetoResto vetoPayload) {
		if (vetoPayload == null || vetoPayload.getRestoIds() == null) {
			return new HashSet<>();
		}
		Set<Integer> ids = rouletteRepository.removeRestos(sessionId, vetoPayload);

		return getRestosDtosByIds(ids);
	}

	public RestoDto randomWinnerResto(String sessionId) {
		Set<RestoDto> remainingRestos = this.getRestoBySession(sessionId);
		if (remainingRestos == null || remainingRestos.isEmpty()) {
			return null;
		}
//		log.info(remainingRestos.toString());
		Random random = new Random();
		int randomIndex = random.nextInt(remainingRestos.size());

		return remainingRestos.stream().toList().get(randomIndex);
	}

	public Set<RestoDto> getRestoBySession(String sessionId) {

		Set<Integer> ids = rouletteRepository.getRestosBySession(sessionId);
		return getRestosDtosByIds(ids);
	}

	private Set<RestoDto> getRestosDtosByIds(Set<Integer> ids) {
		if (ids == null) {
			return new HashSet<>();
		}

		Set<RestoDto> restoDtos = new HashSet<>();
		List<Restaurant> restos = restoService.getRestosBasicInfoByIds(ids);
//		log.info("Found {} restos for this session", restoDtos);
		for (Restaurant resto : restos) {
			restoDtos.add(new RestoDto(resto));
		}
		return restoDtos;
	}
}
