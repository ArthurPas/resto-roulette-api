package com.roulette.resto.repository.social;

import com.roulette.resto.dao.resto.RestoDao;
import com.roulette.resto.dao.social.InteractionDao;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class InteractionRepository {
	final InteractionDao  interactionDao;
	private final RestoDao restoDao;

	public InteractionRepository(InteractionDao interactionDao, RestoDao restoDao) {
		this.interactionDao = interactionDao;
		this.restoDao = restoDao;
	}


	public List<SocialInteraction> getInteractionsByAccountId(int id) {
		return interactionDao.getInteractionsByAccountId(id);
	}

	public boolean addLikeToResto(String restoId, int accountId) {
		return interactionDao.setLikeToResto(Integer.parseInt(restoId), accountId);
	}

	public boolean removeLiketoResto(String restoId, int accountId) {
		return interactionDao.removeLikeToResto(Integer.parseInt(restoId), accountId);
	}

	public String addCommentToResto(String restoId, int accountId, NewComment comment) {
		int commentId = interactionDao.newCommentByUserToResto(Integer.parseInt(restoId),accountId,comment);
		return interactionDao.getCommentById(commentId);
	}

	public Set<Integer> getLikedRestoByAccountId(int accountId) {
		return new HashSet<>(interactionDao.getLikedRestos(accountId));
	}

	public List<Restaurant> getRestosBasicInfoByIds(Set<Integer> ids) {
		return restoDao.getRestosBasicInfoByIds(ids);
	}
}
