package com.roulette.resto.resto.entity;

import com.roulette.resto.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
	int id;
	String displayName;
	String name;
	List<Food> foodType;
	String address;
	BigDecimal longitude;
	BigDecimal latitude;
	Account owner;
	Date creationDate;
}
