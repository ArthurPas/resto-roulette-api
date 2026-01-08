package com.roulette.resto.repository.social;

import com.roulette.resto.dao.resto.RestoDao;
import com.roulette.resto.dao.social.InteractionDao;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.exception.APIError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class InteractionRepository {
	final InteractionDao  interactionDao;

	public InteractionRepository(InteractionDao interactionDao, RestoDao restoDao) {
		this.interactionDao = interactionDao;
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

	public int addCommentToResto(String activity_id, int accountId, NewComment comment) {
		return interactionDao.newCommentByUserToResto(Integer.parseInt(activity_id),accountId,comment);
	}

	public Set<Integer> getLikedRestoByAccountId(int accountId) {
		return new HashSet<>(interactionDao.getLikedRestos(accountId));
	}

	public int editComment(String commentId, String comment) {
		return interactionDao.editComment(Integer.parseInt(commentId), comment);
	}

	public boolean deleteComment(String commentId) {
		String comment = interactionDao.getCommentById(Integer.parseInt(commentId));
		if(comment == null) {
			throw new APIError(14, HttpStatus.NOT_FOUND);
		}
		return interactionDao.deleteComment(Integer.parseInt(commentId));
	}

}
