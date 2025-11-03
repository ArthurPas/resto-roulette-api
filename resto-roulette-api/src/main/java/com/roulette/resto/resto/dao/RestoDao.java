package com.roulette.resto.resto.dao;

import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.entity.Food;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.entity.mapper.RestoRowMapper;
import com.roulette.resto.social.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class RestoDao {
	final JdbcTemplate jdbcTemplate;
	final AccountDao accountDao;
	public RestoDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDao = accountDao;
	}

	public int createResto(Restaurant restaurant) {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		Date createdDate = new Date(System.currentTimeMillis());
		String query = "INSERT INTO resto (display_name, owner_id, created_at) " +
				"VALUES (?, ?, ?)";
		try {
			int accountId = accountDao.getAccountId(restaurant.getOwner());
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, restaurant.getDisplayName());
				preparedStatement.setInt(2, accountId);
				preparedStatement.setDate(3, createdDate);
				return preparedStatement;
			}, generatedKeyHolder);
			int restoId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
			this.createRestoInfos(restaurant, restoId);
			log.warn(restaurant.getFoodType().toString());
			this.linkFoodsType(restaurant.getFoodType(), restoId);
			return restoId;
		} catch (DuplicateKeyException e) {
			throw e;
		}
		} catch (AccountNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void linkFoodsType(List<Food> food, int restoId) throws DuplicateKeyException {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		for (Food foodItem : food) {
			String query = "INSERT INTO resto_resto_type (resto_id,type_id) " +
					"VALUES (?, (SELECT id FROM resto_type WHERE food_type = ?))";
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, restoId);
				preparedStatement.setString(2, foodItem.toString());
				log.warn(preparedStatement.toString());
				return preparedStatement;
			}, generatedKeyHolder);
		}

	}

	private void createRestoInfos(Restaurant restaurant, int restoId){
		String query = "INSERT INTO resto_info (name, address, lon, lat, resto_id) " +
				"VALUES (?, ?, ?, ?, ?)";
		try {
				jdbcTemplate.update(conn -> {
					PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, restaurant.getName());
					preparedStatement.setString(2, restaurant.getAddress());
					preparedStatement.setBigDecimal(3, restaurant.getLongitude());
					preparedStatement.setBigDecimal(4, restaurant.getLatitude());
					preparedStatement.setInt(5,restoId);
					return preparedStatement;
				});
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}

	}

	public Restaurant getRestoById(int restoId) throws RestoNotFoundException {
/*		String query = "SELECT resto_info.resto_id, name, address, lon, lat, resto.resto_id, display_name, owner_id, " +
				"created_at, GROUP_CONCAT(food_table.food_type SEPARATOR ',') AS aggregated_food_types " +
				"FROM resto "+
				" LEFT JOIN resto_info ON resto.resto_id = resto_info.resto_id " +
				" LEFT JOIN  resto_resto_type ON resto.resto_id = resto_resto_type.resto_id " +
				" LEFT JOIN resto_type as food_table ON resto_resto_type.type_id = food_table.id "+
				"WHERE resto.resto_id = ?";*/
		String query = "SELECT  resto.resto_id, display_name, owner_id, created_at, name, address, lon, lat,GROUP_CONCAT" +
				"(food_table" +
				".food_type SEPARATOR ',') AS aggregated_food_types " +
				"FROM " +
				"resto " +
				"JOIN resto_roulette.resto_info ON resto.resto_id = resto_info.resto_id " +
				" JOIN  resto_resto_type ON resto.resto_id = resto_resto_type.resto_id " +
				" JOIN resto_type as food_table ON resto_resto_type.type_id = food_table.id "+
				" WHERE resto.resto_id = ?";
		try {
			return jdbcTemplate.queryForObject(query, new RestoRowMapper(accountDao), restoId);
		}catch (NullPointerException e){
			log.warn(e.getMessage());
			throw new RestoNotFoundException("Restaurant not found");
		}catch (Exception e) {
			log.warn(e.getMessage());
			throw e;
		}
	}
}
