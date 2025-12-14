package com.roulette.resto.data.resto.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.resto.entity.BusinessHour;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.data.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import static com.roulette.resto.service.common.MediaService.buildMediaUrl;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestoDto {
	int id;
	String displayName;
	String name;
	Set<String> foodTypes;
	Set<String> labels;
	String address;
	List<BusinessHour> businessHours;
	Date creationDate;
	Account owner;
	List<MediaResponse> medias;
	List<String> comments;

	public RestoDto(Restaurant restaurant) {
		this.id = restaurant.getId();
		this.displayName = restaurant.getDisplayName();
		this.name = restaurant.getName();
		this.foodTypes = restaurant.getFoodTypes();
		this.labels = restaurant.getLabels();
		this.address = restaurant.getAddress();
		this.businessHours = restaurant.getBusinessHours();
		this.creationDate = restaurant.getCreationDate();
		this.owner = restaurant.getOwner();
		this.medias = buildMediaUrl(restaurant.getMedias());
		this.comments = restaurant.getInteractions().stream().map(SocialInteraction::getComment).toList();
	}
}