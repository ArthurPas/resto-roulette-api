package com.roulette.resto.data.social.dto.out;

import com.roulette.resto.data.social.dto.MinimalAccountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoWithAccount {
	public String commentId;
	public String commentText;
	public MinimalAccountInfo author;

	public  CommentInfoWithAccount(CommentInfo comment) {
		this.commentId = comment.getCommentId();
		this.commentText = comment.getCommentText();
	}
}
