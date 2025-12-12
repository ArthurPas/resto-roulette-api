package com.roulette.resto.repository.social;

import com.roulette.resto.dao.social.InteractionDao;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class InteractionRepository {
	final InteractionDao  interactionDao;

	public InteractionRepository(InteractionDao interactionDao) {
		this.interactionDao = interactionDao;
	}

	public List<SocialInteraction> getInteractionsByAccountLogin(String login) {
		return interactionDao.getInteractionsByAccountLogin(login);

	}

	public List<SocialInteraction> getInteractionsByAccountId(int id) {
		return interactionDao.getInteractionsByAccountId(id);
	}

	public boolean addLikeToResto(String restoId, int accountId) {
		return interactionDao.setLikeToResto(restoId, accountId);
	}
}
