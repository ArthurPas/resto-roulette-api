package com.roulette.resto.data.social.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikedResto {
	int restoId;
	int accountId;
	boolean isLiked;
}
