package com.roulette.resto.resto.dto.in;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class NewRestaurant {
	String displayName;
	String name;
	Set<String> foodTypes;
	Set<String> labels;
	String address;
	String loginOwner;
}
