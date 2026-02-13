package com.roulette.resto.service.roulette;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.roulette.websocket.*;
import com.roulette.resto.repository.roulette.RouletteRepository;
import com.roulette.resto.service.resto.RestoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouletteService {

	final RouletteRepository rouletteRepository;
	final RestoService restoService;

	public RouletteService(RouletteRepository rouletteRepository, RestoService restoService) {
		this.rouletteRepository = rouletteRepository;
		this.restoService = restoService;
	}

	public AccountsInSession addAccountToCurrentSession(String sessionId, JoinSession account) {
		return rouletteRepository.addAccountIdToSession(sessionId, account.getAccountId());

	}

	public RouletteSession createNewSession() {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		return rouletteRepository.createNewSession(sessionId);
	}

	public String getSessionIdByShortId(String shortId) {
		return rouletteRepository.getSessionIdByShortId(shortId);
	}

	public void addFoodChoices(String sessionId, AccountChoices choices) {
		rouletteRepository.addFoodChoices(sessionId, choices);
	}

	public AccountsInSession getAccountsStatus(String sessionId) {
		RouletteSession rouletteSession = rouletteRepository.getSessionById(sessionId);
		List<Integer> accountsId = rouletteSession.getAccountChoices().stream().map(AccountChoices::getAccountId).collect(Collectors.toList());
		List<Integer> accountsIdSwiped = rouletteSession.getAccountChoices().stream()
				.filter(ac -> !ac.getFoodLiked().isEmpty() || !ac.getFoodDisliked().isEmpty())
				.map(AccountChoices::getAccountId)
				.toList();
		List<Integer> accountsIdVeto = rouletteSession.getAccountChoices().stream()
				.filter(AccountChoices::isVetoDone)
				.map(AccountChoices::getAccountId)
				.toList();
		AccountsInSession accountsInSession = new AccountsInSession();
		accountsInSession.setAccountsJoinedIds(accountsId);
		accountsInSession.setAccountsSwipedIds(accountsIdSwiped);
		accountsInSession.setAccountsVetoIds(accountsIdVeto);
		return accountsInSession;
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

	public void saveMatchingRestos(List<RestoDto> restoDtos, String sessionId) {
		Set<Integer> restoIds = new HashSet<>();
		for (RestoDto restoDto : restoDtos) {
			restoIds.add(restoDto.getId());
		}
		rouletteRepository.saveMatchingRestos(restoIds,sessionId);
	}

	public List<RestoDto> removeResto(String sessionId, VetoResto vetoPayload) {
		if (vetoPayload == null || vetoPayload.getRestoIds() == null) {
			return new ArrayList<>();
		}
		Set<Integer> ids = rouletteRepository.removeRestos(sessionId, vetoPayload);

		return getRestosDtosByIds(ids);
	}

	public RestoDto randomWinnerResto(List<RestoDto> remainingRestos) {
		if (remainingRestos == null || remainingRestos.isEmpty()) {
			return null;
		}
		Random random = new Random();
		int randomIndex = random.nextInt(remainingRestos.size());

		return remainingRestos.get(randomIndex);
	}

	public List<RestoDto> getRestoBySession(String sessionId) {

		Set<Integer> ids = rouletteRepository.getRestosBySession(sessionId);
		return getRestosDtosByIds(ids);
	}

	private List<RestoDto> getRestosDtosByIds(Set<Integer> ids) {
		if (ids == null) {
			return new ArrayList<>();
		}

		List<RestoDto> restoDtos = new ArrayList<>();
		List<Restaurant> restos = restoService.getRestosBasicInfoByIds(ids);

		for (Restaurant resto : restos) {
			restoDtos.add(new RestoDto(resto));
		}
		return restoDtos;
	}
}
