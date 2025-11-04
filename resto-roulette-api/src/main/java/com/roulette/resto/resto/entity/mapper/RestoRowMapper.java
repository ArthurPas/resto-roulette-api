package com.roulette.resto.resto.entity.mapper;

import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.social.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
@Slf4j
public class RestoRowMapper implements RowMapper<Restaurant> {
	public final AccountDao accountDao;

	public RestoRowMapper(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
	@Override
	public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(rs.getInt("resto_id"));
		restaurant.setName(rs.getString("name"));
		restaurant.setDisplayName(rs.getString("display_name"));
		restaurant.setOwner(accountDao.getAccountById(rs.getInt("owner_id")));
		restaurant.setLongitude(rs.getBigDecimal("lon"));
		restaurant.setLatitude(rs.getBigDecimal("lat"));
		restaurant.setAddress(rs.getString("address"));
		String aggregatedTypes = rs.getString("aggregated_food_types");
		List<String> types = new ArrayList<>();

		if (aggregatedTypes != null && !aggregatedTypes.isEmpty()) {
			String[] typeArray = aggregatedTypes.split(",");
			for (String typeStr : typeArray) {
				try {
					types.add(typeStr.trim().toUpperCase());
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				}
			}
		}

		restaurant.setFoodType(types);
		log.warn(restaurant.toString());
		return restaurant;
	}
}