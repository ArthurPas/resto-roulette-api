package com.roulette.resto.resto.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Restaurant {
	int id;
	String displayName;
	String name;
	Set<String> foodTypes;
	Set<String> labels;
	String address;
	BigDecimal longitude;
	BigDecimal latitude;
	List<BusinessHour> businessHours;
	Date creationDate;
	Account owner;
	List<MediaResource> medias;
}
