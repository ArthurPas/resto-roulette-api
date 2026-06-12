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
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.repository.social.InteractionRepository;
import com.roulette.resto.service.resto.RestoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
//			@JsonProperty("isFollower") boolean isFollower,
//			@JsonProperty("isFollowed") boolean isFollowed,
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

	public FollowersStatus getFollowersStatus(Account account, Account otherAccountId){
		return getFollowersStatus(new MinimalAccountInfo(account), new MinimalAccountInfo(otherAccountId));
	}

	public FollowersStatus getFollowersStatus(MinimalAccountInfo account, MinimalAccountInfo otherAccountId)  {
		boolean isFriend =
				accountRepository.getFollowersByAccountId(account.getAccountId()).contains(otherAccountId);
		boolean isAsking  =
				accountRepository.getIngoingFollowingRequest(account.getAccountId()).contains(otherAccountId);
		boolean isAsked = accountRepository.getIngoingFollowingRequest(otherAccountId.getAccountId()).contains(account);
		if(isFriend) {
			return new FollowersStatus(FollowingStatus.FRIENDS);
		}
		else if(isAsking) {
			return new FollowersStatus(FollowingStatus.INCOMING_REQUEST);
		}
		else if(isAsked) {
			return new FollowersStatus(FollowingStatus.OUTGOING_REQUEST);
		}
		else  {
			return new FollowersStatus(FollowingStatus.NONE);
		}
	}
}


