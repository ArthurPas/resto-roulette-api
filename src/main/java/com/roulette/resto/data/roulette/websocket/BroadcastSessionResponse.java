package com.roulette.resto.data.roulette.websocket;

import com.roulette.resto.data.resto.dto.out.RestoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastSessionResponse {
	SessionStatus status = SessionStatus.LOBBY;
	List<String> accountsInSession = new ArrayList<>();
	List<String> accountsRemaining = new ArrayList<>();
	List<RestoDto> restoCandidates  = new ArrayList<>();
	RestoDto winner;
}
