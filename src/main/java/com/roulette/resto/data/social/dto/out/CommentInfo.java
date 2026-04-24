package com.roulette.resto.data.social.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfo {
	public String commentId;
	public String commentText;
	public int authorId;
	public int activityId;
}
