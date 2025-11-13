package com.roulette.resto.resto.dto.in;

import com.roulette.resto.resto.entity.BusinessHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBusinessHours {
	int restoId;
	List<BusinessHour> businessHours;
}
