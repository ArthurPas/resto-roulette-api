package com.roulette.resto.service.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roulette.resto.data.social.dto.MinimalAccountInfo;
import com.roulette.resto.data.social.dto.UserLikedRestos;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.out.LikedResto;
import com.roulette.resto.data.social.dto.SocialInteraction;
import com.roulette.resto.data.social.dto.out.SocialInteractionResponse;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.FollowingStatus;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.resto.RestoRepository;
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.repository.social.InteractionRepository;
import com.roulette.resto.service.resto.RestoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SocialService {

	final InteractionRepository interactionRepository;
	private final RestoService restoService;
	private final AccountRepository accountRepository;
	public record FollowersStatus(
			@JsonProperty("isFollower") boolean isFollower,
			@JsonProperty("isFollowed") boolean isFollowed,
			@JsonProperty("followingStatus") FollowingStatus followingStatus
	) {}
	public SocialService(InteractionRepository interactionRepository, RestoService restoService, AccountRepository accountRepository) {
		this.interactionRepository = interactionRepository;
		this.restoService = restoService;
		this.accountRepository = accountRepository;
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

	public int addComment(String activity_id, int accountId, NewComment comment) {
		return interactionRepository.addCommentToResto(activity_id, accountId, comment);
	}
	public int editComment(String commentId, @Valid NewComment comment) {
//		log.info(commentId);
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

	public List<SocialInteractionResponse> getInteractionsByAccountId(int accountId) {
		List<SocialInteraction> socialInteractions = interactionRepository.getInteractionsByAccountId(accountId);

		List<SocialInteractionResponse> socialInteractionResponses = new ArrayList<>();
		for (SocialInteraction  socialInteraction : socialInteractions) {
			SocialInteractionResponse socialInteractionResponse = new SocialInteractionResponse(socialInteraction);
			socialInteractionResponse.setRestoInfo(restoService.getMinimalRestoInfo(String.valueOf(socialInteraction.getRestoId())));
			socialInteractionResponses.add(socialInteractionResponse);
		}
		return socialInteractionResponses;
	}

	public FollowersStatus getFollowersStatus(Account account, Account otherAccountId)  {
		boolean isFollowed =
				accountRepository.getFollowersByAccountId(account.getAccountId()).contains(new MinimalAccountInfo(otherAccountId));
		boolean isFollower =
				accountRepository.getFollowersByAccountId(otherAccountId.getAccountId()).contains(new MinimalAccountInfo(account));
		if(isFollowed && isFollower) {
			return new FollowersStatus(true, true, FollowingStatus.BOTH_FOLLOW);
		}
		else if(isFollowed) {
			return new FollowersStatus(false, true, FollowingStatus.FOLLOWING);
		}
		else if(isFollower) {
			return new FollowersStatus(true, false, FollowingStatus.FOLLOW_ME);
		}
		else  {
			return new FollowersStatus(false, false, FollowingStatus.NONE);
		}
	}

}


