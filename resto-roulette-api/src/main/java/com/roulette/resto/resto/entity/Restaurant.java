package com.roulette.resto.resto.entity;

import com.roulette.resto.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
	String displayName;
	String name;
	String type;
	List<Food> foodType;
	String address;
	Float longitude;
	Float latitude;
	Account owner;
	Date creationDate;
}
