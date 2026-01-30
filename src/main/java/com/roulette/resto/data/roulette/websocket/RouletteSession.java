package com.roulette.resto.data.roulette.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouletteSession {
	String sessionId;
	String shortId;
	List<AccountChoices> accountChoices = new ArrayList<>();
}