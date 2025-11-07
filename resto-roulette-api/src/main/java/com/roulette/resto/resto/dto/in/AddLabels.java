package com.roulette.resto.resto.dto.in;

import com.roulette.resto.resto.entity.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddLabels {
	String restoId;
	List<Label> labels;
}
