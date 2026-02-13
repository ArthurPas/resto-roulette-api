package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountChoices {
	String login;
	Set<String> foodLiked;
	Set<String> foodDisliked;
	boolean vetoDone;
}
