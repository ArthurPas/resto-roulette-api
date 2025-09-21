package com.roulette.resto.business.social.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resto {
	public int id;
	public String displayName;
	public String type; //TODO: transform in enum
	public Account owner;

}
