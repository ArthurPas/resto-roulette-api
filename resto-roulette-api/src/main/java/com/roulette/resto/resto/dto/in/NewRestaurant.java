package com.roulette.resto.resto.dto.in;

import lombok.Data;

import java.util.List;

@Data
public class NewRestaurant {
	String displayName;
	String name;
	String type;
	List<String> foodTypes;
	String address;
	Float longitude;
	Float latitude;
	String loginOwner;
}
