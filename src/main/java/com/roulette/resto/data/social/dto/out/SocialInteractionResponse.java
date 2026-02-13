package com.roulette.resto.data.social.dto.out;

import com.roulette.resto.data.resto.dto.out.MinimalRestoInfo;
import com.roulette.resto.data.social.dto.SocialInteraction;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SocialInteractionResponse {
	Integer accountId;
	int activityId;
	String comment;
	Integer commentId;
	MinimalRestoInfo restoInfo;

	public SocialInteractionResponse(SocialInteraction socialInteraction) {
		this.accountId = socialInteraction.getAccountId();
		this.activityId = socialInteraction.getActivityId();
		this.comment = socialInteraction.getComment();
		this.commentId = socialInteraction.getCommentId();
	}
}
