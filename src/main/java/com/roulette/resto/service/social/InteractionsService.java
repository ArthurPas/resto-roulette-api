package com.roulette.resto.service.social;

import com.roulette.resto.data.social.UserLikedRestos;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.repository.social.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InteractionsService {

	final InteractionRepository interactionRepository;

	public InteractionsService(InteractionRepository interactionRepository) {
		this.interactionRepository = interactionRepository;
	}

	public LikedResto likeResto(String restoId, int accountId) {
		LikedResto likedResto = new LikedResto();
		likedResto.setRestoId(Integer.parseInt(restoId));
		likedResto.setAccountId(accountId);
		try {
			boolean liked = interactionRepository.addLikeToResto(restoId,accountId);
			likedResto.setLiked(liked);
			return likedResto;
		}catch (DuplicateKeyException e) {
			likedResto.setLiked(true);
		}
		return likedResto;
	}

	public LikedResto dislikeResto(String restoId, int accountId) {
		LikedResto likedResto = new LikedResto();
		likedResto.setRestoId(Integer.parseInt(restoId));
		likedResto.setAccountId(accountId);
		boolean liked = interactionRepository.removeLiketoResto(restoId,accountId);
		likedResto.setLiked(liked);
		return likedResto;
	}

	public NewComment addComment(String restoId, int accountId, NewComment comment) {
		String commentInserted = interactionRepository.addCommentToResto(restoId, accountId, comment);
		return new NewComment(commentInserted);
	}

	public UserLikedRestos getUserLikedRestoIds(int accountId) {
		UserLikedRestos likedResto = new UserLikedRestos();
		likedResto.setAccountId(accountId);
		List<Integer> restoIds = interactionRepository.getLikedRestoByAccountId(accountId);
		likedResto.setRestoIds(restoIds);
		return likedResto;
	}

	public List<SocialInteraction> getInteractionsByAccountId(int accountId) {
		List<SocialInteraction> socialInteraction = interactionRepository.getInteractionsByAccountId(accountId);
		List<Integer> restosLiked = getUserLikedRestoIds(accountId).getRestoIds();
		for (SocialInteraction interaction : socialInteraction) {
			if (restosLiked.contains(interaction.getRestoId())) {
				interaction.setHas_liked(true);
			}
		}
		return socialInteraction;
	}
}
