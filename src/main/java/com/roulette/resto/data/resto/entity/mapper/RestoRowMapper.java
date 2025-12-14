package com.roulette.resto.data.resto.entity.mapper;

import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.data.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class RestoRowMapper implements RowMapper<Restaurant> {
	@Override
	public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(rs.getInt("resto_id"));
		restaurant.setName(rs.getString("name"));
		restaurant.setDisplayName(rs.getString("display_name"));
		restaurant.setLongitude(rs.getBigDecimal("lon"));
		restaurant.setLatitude(rs.getBigDecimal("lat"));
		restaurant.setAddress(rs.getString("address"));
		String aggregatedTypes = rs.getString("aggregated_food_types");
		restaurant.setFoodTypes(aggregatedToSet(aggregatedTypes));
		String aggregatedLabels = rs.getString("aggregated_labels");
		restaurant.setLabels(aggregatedToSet(aggregatedLabels));
		String aggregatedComments = rs.getString("aggregated_comments");
		restaurant.setInteractions(splitCommentsIntoSocialInteractions(aggregatedComments));
		restaurant.setCreationDate(rs.getDate("created_at"));
		return restaurant;
	}

	private Set<String> aggregatedToSet(String aggregatedResult) {
		Set<String> result = new HashSet<>();
		if (aggregatedResult != null && !aggregatedResult.isEmpty()) {
			String[] aggregatedResultAsArr = aggregatedResult.split(",");
			for (String resultStr : aggregatedResultAsArr) {
				try {
					result.add(resultStr.trim().toUpperCase());
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				}
			}
		}
		return result;
	}
	private List<SocialInteraction> splitCommentsIntoSocialInteractions(String aggregated_comments) {
		if(aggregated_comments == null) {
			return Collections.emptyList();
		}
		List<SocialInteraction> socialInteractions = new ArrayList<>();
		for (String resultStr : aggregated_comments.split(";;")) {
			SocialInteraction socialInteraction = new SocialInteraction();
			socialInteraction.setComment(resultStr.trim());
			socialInteractions.add(socialInteraction);
		}
		return socialInteractions;
	}
}