package com.roulette.resto.data.resto.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.social.dto.SocialInteraction;
import com.roulette.resto.data.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Restaurant {
	Integer id;
	String displayName;
	String name;
	Set<String> foodTypes;
	Set<String> labels;
	String address;
	double longitude;
	double latitude;
	List<BusinessHour> businessHours;
	Date creationDate;
	Account owner;
	List<MediaResource> medias;
	List<SocialInteraction> interactions;
	VerificationStatus verificationStatus;
}
