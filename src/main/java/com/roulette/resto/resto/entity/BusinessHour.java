package com.roulette.resto.resto.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHour {
	int weekDay;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm",timezone="Europe/Paris")
	@Schema(example = "11:30")
	Date openingHour;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm",timezone="Europe/Paris")
	@Schema(example = "13:30")
	Date closingHour;
	boolean isLunch;
}
