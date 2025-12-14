package com.roulette.resto.service.social;

import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.UserLikedRestos;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.resto.RestoRepository;
import com.roulette.resto.repository.social.InteractionRepository;
import com.roulette.resto.service.resto.RestoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class InteractionsService {

	final InteractionRepository interactionRepository;
	private final RestoService restoService;

	public InteractionsService(InteractionRepository interactionRepository, RestoService restoService, RestoRepository restoRepository) {
		this.interactionRepository = interactionRepository;
		this.restoService = restoService;
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

	public int addComment(String restoId, int accountId, NewComment comment) {
		return interactionRepository.addCommentToResto(restoId, accountId, comment);
	}
	public int editComment(String commentId, @Valid NewComment comment) {
		log.info(commentId);
		return interactionRepository.editComment(commentId, comment.getComment());
	}

	public void deleteComment(String commentId) {
		boolean success = interactionRepository.deleteComment(commentId);
		if(!success) {
			throw new APIError(15, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public UserLikedRestos getUserLikedRestoIds(int accountId) {
		UserLikedRestos likedResto = new UserLikedRestos();
		likedResto.setAccountId(accountId);
		Set<Integer> restoIds = interactionRepository.getLikedRestoByAccountId(accountId);
		likedResto.setRestoIds(restoIds);
		return likedResto;
	}

	public List<SocialInteraction> getInteractionsByAccountId(int accountId) {
		List<SocialInteraction> socialInteractions = interactionRepository.getInteractionsByAccountId(accountId);
		Set<Integer> restosLiked = getUserLikedRestoIds(accountId).getRestoIds();
		for (SocialInteraction interaction : socialInteractions) {
			if(restosLiked.contains(interaction.getRestoId())) {
				interaction.setHas_liked(true);
				restosLiked.removeIf(id -> Objects.equals(interaction.getRestoId(), id));
			}
		}
		List<Restaurant> restaurantsLikedNotCommented = restoService.getRestosBasicInfoByIds(restosLiked);
		if(!restaurantsLikedNotCommented.isEmpty()){
			for (Restaurant restaurant : restaurantsLikedNotCommented) {
				SocialInteraction socialInteraction = new SocialInteraction();
				socialInteraction.setRestoId(restaurant.getId());
				socialInteraction.setHas_liked(true);
				socialInteraction.setRestoName(restaurant.getDisplayName());
				socialInteraction.setAccountId(accountId);
				socialInteractions.add(socialInteraction);
			}
		}
		return socialInteractions;
	}

}


