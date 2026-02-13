package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsInSession {
	List<Integer> accountsJoinedIds;
	List<Integer> accountsSwipedIds;
	List<Integer> accountsVetoIds;
}
