package com.roulette.resto.data.roulette.websocket;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastSessionResponse {
	SessionStatus status = SessionStatus.LOBBY;
	Set<String> accountsInSession = new HashSet<>();
	Set<String> accountsRemaining = new HashSet<>();
	Set<RestoDto> restoCandidates  = new HashSet<>();
	RestoDto winner;
}
