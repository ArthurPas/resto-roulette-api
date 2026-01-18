package com.roulette.resto.data.roulette.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewSessionDto {
	List<Integer> participantsIds;
	String description;
	int restoId;
}
