package com.roulette.resto.service.social;

import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.repository.social.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
}
