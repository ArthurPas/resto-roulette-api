package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountChoices {
	int accountId;
	List<String> foodLiked;
	List<String> foodDisliked;
}
