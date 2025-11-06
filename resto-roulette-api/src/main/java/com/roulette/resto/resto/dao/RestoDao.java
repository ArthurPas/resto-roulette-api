package com.roulette.resto.resto.dao;

import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dto.in.NewBusinessHours;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.entity.mapper.BusinessHoursRowMapper;
import com.roulette.resto.resto.entity.mapper.RestoRowMapper;
import com.roulette.resto.social.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

	private void linkFoodsType(List<String> foodTypes, int restoId) throws DuplicateKeyException {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		log.warn(foodTypes.toString());
		for (String foodType: foodTypes) {
			String query = "INSERT INTO resto_resto_type (resto_id,type_id) " +
					"VALUES (?, (SELECT id FROM resto_type WHERE food_type = ?))";
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, restoId);
				preparedStatement.setString(2, foodType);
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
		String query = "SELECT  resto.resto_id, resto.owner_id, display_name, owner_id, created_at, name, address, " +
				"lon, lat," +
				"GROUP_CONCAT" +
				"(food_table" +
				".food_type SEPARATOR ',') AS aggregated_food_types " +
				"FROM " +
				"resto " +
				"INNER JOIN resto_roulette.resto_info ON resto.resto_id = resto_info.resto_id " +
				"INNER JOIN  resto_resto_type ON resto.resto_id = resto_resto_type.resto_id " +
				"INNER JOIN resto_type as food_table ON resto_resto_type.type_id = food_table.id "+
				"WHERE resto.resto_id = ?";
		try {
			return jdbcTemplate.queryForObject(query, new RestoRowMapper(accountDao), restoId);
		}catch (NullPointerException e){
			log.warn(e.getMessage());
			throw new RestoNotFoundException("Restaurant not found");
		}
	}

	public void createFoodType(String foodType) {
		String query = "INSERT INTO resto_type (food_type) " +
				"VALUES (?)";
		try {
			jdbcTemplate.update(query, foodType);
		}catch (NullPointerException e){
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<String> getFoodTypes() {
		String query = "SELECT food_type FROM resto_type";
		try {
			return jdbcTemplate.queryForList(query, String.class);
		}catch (NullPointerException e){
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<String> getFoodType(String foodType) {
		String query = "SELECT food_type FROM resto_type where food_type like ?";
		try {
			return jdbcTemplate.queryForList(query,String.class, '%'+foodType+'%');
		}catch (NullPointerException e){
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<Restaurant> getAllRestos(int limit, int offset) {
			String query =
				"SELECT  resto.resto_id, resto.owner_id, display_name, owner_id, created_at, name, " +
				"address,lon, lat,GROUP_CONCAT(food_table.food_type SEPARATOR ',') AS aggregated_food_types " +
				"FROM resto " +
				"INNER JOIN resto_roulette.resto_info ON resto.resto_id = resto_info.resto_id " +
				"INNER JOIN  resto_resto_type ON resto.resto_id = resto_resto_type.resto_id " +
				"INNER JOIN resto_type as food_table ON resto_resto_type.type_id = food_table.id "+
				" GROUP BY resto_roulette.resto.resto_id "+
				"ORDER BY resto.resto_id " +
				"LIMIT ? "+
				"OFFSET ? ";
			try {
				return jdbcTemplate.query(query, new RestoRowMapper(accountDao),  limit, offset);
			}catch (EmptyResultDataAccessException e){
				log.warn(e.getMessage());
				return null;
			}
		}
	
	public List<BusinessHour> getBusinessHoursByRestoId(int restoId) {
		String query = "SELECT * FROM business_hour WHERE resto_id = ?";
		return jdbcTemplate.query(query, new BusinessHoursRowMapper(), restoId);
	}
		
	@Transactional
	public List<BusinessHour>  addBusinessHoursToResto(NewBusinessHours businessHours) {
		log.warn(businessHours.toString());
		int restoId = businessHours.getRestoId();
		List<String> queries = new ArrayList<>();
		for (BusinessHour businessHour: businessHours.getBusinessHours()){

			LocalTime openHours = convertToLocalTime(businessHour.getOpeningHour());
			LocalTime closingHours = convertToLocalTime(businessHour.getClosingHour());
			String query = "INSERT INTO resto_roulette.business_hour (resto_id, week_day, opening_hour, closing_hour)" +
					"VALUES (?, ?, ?, ?)";
			jdbcTemplate.update(query, restoId, businessHour.getWeekDay(), openHours, closingHours);
		}
		return getBusinessHoursByRestoId(restoId);
	}

	private static LocalTime convertToLocalTime(
			java.util.Date businessHour) {
		LocalTime hours = businessHour.toInstant()
				.atZone(ZoneId.of("Europe/Paris"))
				.toLocalTime();
		return hours.truncatedTo(ChronoUnit.MINUTES);
	}
}