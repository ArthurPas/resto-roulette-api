package com.roulette.resto.data.resto.dto.in;

import com.roulette.resto.data.resto.entity.BusinessHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBusinessHours {
	List<BusinessHour> businessHours;
}
