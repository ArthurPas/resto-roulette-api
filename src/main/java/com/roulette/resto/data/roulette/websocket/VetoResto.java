package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetoResto {
	private Set<Integer> restoIds;
	private String login;
}