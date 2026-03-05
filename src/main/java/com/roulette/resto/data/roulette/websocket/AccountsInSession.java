package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsInSession {
	List<String> accountsJoined;
	List<String> accountsSwiped;
	List<String> accountsVeto;
}
