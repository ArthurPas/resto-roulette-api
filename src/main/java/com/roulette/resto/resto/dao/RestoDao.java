package com.roulette.resto.resto.dao;

import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dto.in.NewBusinessHours;
import com.roulette.resto.resto.dto.in.NewRestaurant;
import com.roulette.resto.resto.dto.in.UpdateBusinessHours;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.entity.mapper.BusinessHoursRowMapper;
import com.roulette.resto.resto.entity.mapper.RestoRowMapper;
import com.roulette.resto.social.dao.AccountDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.security.auth.login.AccountNotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@Slf4j
public class RestoDao {
	final JdbcTemplate jdbcTemplate;
	final AccountDao accountDao;
	@Value("${app.storage.images.location}")
	private String imageDir;
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
			String query = "INSERT INTO resto_resto_types (resto_id,type_id) " +
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
		String query = "SELECT " +
					" resto.resto_id," +
					" resto.owner_id," +
					" display_name," +
					" created_at," +
					" name," +
					" address," +
					" lon," +
					" lat," +
					" (SELECT GROUP_CONCAT(food_table.food_type SEPARATOR ',')" +
					"  FROM resto_resto_types" +
					"  JOIN resto_type as food_table ON resto_resto_types.type_id = food_table.id" +
					"  WHERE resto_resto_types.resto_id = resto_roulette.resto.resto_id" +
					" ) AS aggregated_food_types," +
					" (SELECT GROUP_CONCAT(label_table.label_name SEPARATOR ',')" +
					"  FROM resto_resto_labels" +
					"  JOIN resto_label as label_table ON resto_resto_labels.label_id = label_table.label_id" +
					"  WHERE resto_resto_labels.resto_id = resto.resto_id" +
					" ) AS aggregated_labels " +
					" FROM resto_roulette.resto " +
					"LEFT OUTER JOIN  resto_roulette.resto_info ON resto_roulette.resto.resto_id = resto_info" +
				".resto_id" +
					" WHERE" +
					" resto_roulette.resto.resto_id = ?";
		try {
			return jdbcTemplate.queryForObject(query, new RestoRowMapper(accountDao), restoId);
		}catch (DataAccessException e){
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

	public List<Restaurant> getAllRestos(int limit, int offset) throws RestoNotFoundException {
			String query ="SELECT resto.resto_id," +
				" resto.owner_id," +
				" display_name," +
				" created_at," +
				" name," +
				" address," +
				" lon," +
				" lat," +
				" (SELECT GROUP_CONCAT(food_table.food_type SEPARATOR ',')" +
				"  FROM resto_resto_types" +
				"  JOIN resto_type as food_table ON resto_resto_types.type_id = food_table.id" +
				"  WHERE resto_resto_types.resto_id = resto_roulette.resto.resto_id" +
				" ) AS aggregated_food_types," +
				" (SELECT GROUP_CONCAT(label_table.label_name SEPARATOR ',')" +
				"  FROM resto_resto_labels" +
				"  JOIN resto_label as label_table ON resto_resto_labels.label_id = label_table.label_id" +
				"  WHERE resto_resto_labels.resto_id = resto.resto_id" +
				" ) AS aggregated_labels " +
				" FROM resto_roulette.resto " +
				"LEFT OUTER JOIN  resto_roulette.resto_info ON resto_roulette.resto.resto_id = resto_info" +
				".resto_id" +
				" GROUP BY resto_roulette.resto.resto_id "+
				"ORDER BY resto.resto_id " +
				"LIMIT ? "+
				"OFFSET ? ";
			try {
				return jdbcTemplate.query(query, new RestoRowMapper(accountDao),  limit, offset);
			}catch (EmptyResultDataAccessException e){
				log.warn(e.getMessage());
				throw  new RestoNotFoundException("Resto not found");
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

	public List<BusinessHour> changeBusinessHours(UpdateBusinessHours updateBusinessHours) {
		//If its lunch you only want the first opening hours, if its not you want the last opening hours
		String filter = updateBusinessHours.isLunch() ? "DESC" :  "ASC";
		String query = "UPDATE business_hour " +
				" SET opening_hour = ?, closing_hour = ? " +
				" WHERE week_day = ? AND resto_id = ? " +
				" ORDER BY opening_hour "  + filter +
				" LIMIT 1 ";
		jdbcTemplate.update(query, 
				updateBusinessHours.getOpeningHour(), updateBusinessHours.getClosingHour(), 
				updateBusinessHours.getWeekDay(), updateBusinessHours.getRestoId());
		return getBusinessHoursByRestoId(updateBusinessHours.getRestoId());

	}

	public Restaurant updateResto(int restoId, int ownerId , NewRestaurant newRestaurant) {
		String queryRestoInfo = "UPDATE resto_info SET name = ?, address = ?, lon = ?,  lat = ? WHERE resto_id = ?";
		String queryResto = "UPDATE resto SET display_name = ?, resto.owner_id = ? WHERE resto_id = ?";
		try {
			jdbcTemplate.update(queryRestoInfo,newRestaurant.getName(),newRestaurant.getAddress(), newRestaurant.getLongitude(),
					newRestaurant.getLatitude(), restoId);
			jdbcTemplate.update(queryResto,newRestaurant.getDisplayName(), ownerId,
					restoId);
			return this.getRestoById(restoId);
		} catch (DataAccessException | RestoNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String newLabel(String label) {
		String query = "INSERT INTO resto_label (label_name) " +
				"VALUES (?)";
		try {
			jdbcTemplate.update(query, label);
			return label;
		}catch (NullPointerException e){
			log.error(e.getMessage());
			throw e;
		}
	}

	public int labelExist(String label) {
		String query = "SELECT label_id FROM resto_label WHERE label_name = ?";
		try {
			return jdbcTemplate.queryForObject(query, Integer.class, label);
		}catch (EmptyResultDataAccessException e){
			log.error(e.getMessage());
			return 0;
		}
	}

	public List<String> getAllLabels() {
		String query = "SELECT label_name FROM  resto_label";
		try {
			return jdbcTemplate.queryForList(query, String.class);
		}catch (EmptyResultDataAccessException e){
			return Collections.emptyList();
		}
	}
	
	public List<String> getLabelByRestoId(int restoId) {
		String query = "SELECT label_name FROM resto_label " +
				"JOIN resto_roulette.resto_resto_labels ON resto_label.label_id = resto_resto_labels.label_id " +
				"JOIN resto_roulette.resto r on resto_resto_labels.resto_id = r.resto_id "+
				"WHERE r.resto_id = ?";
		try {
			return jdbcTemplate.queryForList(query, String.class, restoId);
		}catch (EmptyResultDataAccessException e){
			return Collections.emptyList();
		}
	}

	public List<String> addLabelsToResto(int restoId, List<String> labels) {
		try {
			for (String label: labels){
				String query = "INSERT INTO resto_resto_labels (resto_id,label_id) " +
						"VALUES (?, (SELECT label_id FROM resto_label WHERE label_name = ?))";
				jdbcTemplate.update(query, restoId, label);
			}
		}catch (Exception e){
			throw new RuntimeException("Label already exists");
		}
		List<String> restoLabels = getLabelByRestoId(restoId);
		log.info(restoLabels.toString());
		return labels;
	}

	public List<Restaurant> getRestoByOwner(int accountId) {
		String query = "SELECT " +
				" resto.resto_id," +
				" resto.owner_id," +
				" display_name," +
				" created_at," +
				" name," +
				" address," +
				" lon," +
				" lat," +
				" (SELECT GROUP_CONCAT(food_table.food_type SEPARATOR ',')" +
				"  FROM resto_resto_types" +
				"  JOIN resto_type as food_table ON resto_resto_types.type_id = food_table.id" +
				"  WHERE resto_resto_types.resto_id = resto_roulette.resto.resto_id" +
				" ) AS aggregated_food_types," +
				" (SELECT GROUP_CONCAT(label_table.label_name SEPARATOR ',')" +
				"  FROM resto_resto_labels" +
				"  JOIN resto_label as label_table ON resto_resto_labels.label_id = label_table.label_id" +
				"  WHERE resto_resto_labels.resto_id = resto.resto_id" +
				" ) AS aggregated_labels " +
				" FROM resto_roulette.resto " +
				"LEFT OUTER JOIN  resto_roulette.resto_info ON resto_roulette.resto.resto_id = resto_info" +
				".resto_id" +
				" WHERE" +
				" resto_roulette.resto.owner_id = ?";
		return jdbcTemplate.query(query, new RestoRowMapper(accountDao), accountId);
	}

	public void saveImageInDb(int restoId, String uuid) {
		try {
			String query = "INSERT INTO resto_resto_resources (resto_id,resource_id) " +
					"VALUES (?, ?)";
			jdbcTemplate.update(query, restoId, uuid);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	public String saveImage(int restoId, BufferedImage newImage){
		try{
			String uuid = UUID.randomUUID().toString();
			saveImageInDb(restoId, uuid);
			ImageIO.write(newImage, "jpg", new File(imageDir+"/"+uuid+".jpg"));
			return uuid;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getRestoPictureByRestoId(String id) {
		String query = "SELECT resource_id FROM resto_resto_resources " +
				"JOIN resto_roulette.resto r on resto_resto_resources.resto_id = r.resto_id "+
				"WHERE r.resto_id = ?";
		try {
			return jdbcTemplate.queryForList(query, String.class, id);
		}catch (EmptyResultDataAccessException e){
			return Collections.emptyList();
		}
	}
}