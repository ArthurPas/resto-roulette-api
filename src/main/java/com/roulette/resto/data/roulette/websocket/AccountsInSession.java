package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsInSession {
	Set<String> accountsJoined;
	Set<String> accountsSwiped;
	Set<String> accountsVeto;
}
