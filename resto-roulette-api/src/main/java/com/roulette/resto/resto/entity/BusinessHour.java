package com.roulette.resto.resto.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHour {
	int weekDay;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss",timezone="Europe/Paris")
	Date openingHour;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss",timezone="Europe/Paris")
	Date closingHour;
}
