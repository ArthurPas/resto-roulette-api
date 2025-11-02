package com.roulette.resto.resto.dao;

import com.roulette.resto.resto.entity.Food;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.social.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
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

	public Date createResto(Restaurant restaurant) {
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
			this.createRestoInfos(restaurant);
			this.linkFoodsType(restaurant.getFoodType(), restoId);
			return createdDate;
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
			String query = "INSERT INTO resto_resto_type (resto_id,food_type_id) " +
					"VALUES (?, ?)";
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, restoId);
				preparedStatement.setInt(2, foodItem.foodId);
				return preparedStatement;
			}, generatedKeyHolder);
		}

	}

	private void createRestoInfos(Restaurant restaurant) {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		String query = "INSERT INTO resto_info (name, address, lon, lat) " +
				"VALUES (?, ?, ?, ?)";
		try {
				jdbcTemplate.update(conn -> {
					PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, restaurant.getName());
					preparedStatement.setString(2, restaurant.getAddress());
					preparedStatement.setBigDecimal(3, BigDecimal.valueOf(restaurant.getLongitude()));
					preparedStatement.setBigDecimal(4, BigDecimal.valueOf(restaurant.getLatitude()));
					return preparedStatement;
				}, generatedKeyHolder);
				int restoInfos = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}

	}
	
}
