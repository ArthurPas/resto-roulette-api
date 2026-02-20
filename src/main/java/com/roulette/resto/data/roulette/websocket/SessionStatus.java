package com.roulette.resto.data.roulette.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public enum SessionStatus {
	LOBBY,
	SWIPE,
	VETO,
	RESULT;
}
