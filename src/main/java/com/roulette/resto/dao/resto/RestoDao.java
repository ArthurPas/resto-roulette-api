package com.roulette.resto.dao.resto;

import com.roulette.resto.dao.social.AccountDao;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import com.roulette.resto.data.common.entity.mappers.MediaMapper;
import com.roulette.resto.data.resto.dto.in.NewBusinessHours;
import com.roulette.resto.data.resto.dto.in.NewRestaurant;
import com.roulette.resto.data.resto.dto.in.UpdateBusinessHours;
import com.roulette.resto.data.resto.entity.BusinessHour;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.resto.entity.mapper.BusinessHoursRowMapper;
import com.roulette.resto.data.resto.entity.mapper.RestoRowMapper;
import com.roulette.resto.exception.RestoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class RestoDao {
	final JdbcTemplate jdbcTemplate;
	final AccountDao accountDao;
	final DataSource dataSource;

	public RestoDao(JdbcTemplate jdbcTemplate, AccountDao accountDao, DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDao = accountDao;
		this.dataSource = dataSource;
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
			this.linkFoodsType(restoId, restaurant.getFoodTypes());
			this.addLabelsToResto(restoId, restaurant.getLabels());
			return restoId;
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
		} catch (AccountNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void linkFoodsType( int restoId,Set<String> foodTypes) throws DuplicateKeyException {
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
					" resto_roulette.resto.resto_id = ? AND resto.is_deleted = false";
		try {
			return jdbcTemplate.queryForObject(query, new RestoRowMapper(), restoId);
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

	public Set<String> getFoodTypes() {
		String query = "SELECT food_type FROM resto_type";
		try {
			return new HashSet<>(jdbcTemplate.queryForList(query, String.class));
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
				"LEFT OUTER JOIN  resto_roulette.resto_info ON resto_roulette.resto.resto_id = resto_info.resto_id " +
				"WHERE is_deleted = false "+
				" GROUP BY resto_roulette.resto.resto_id "+
				" ORDER BY resto.resto_id " +
				" LIMIT ? "+
				" OFFSET ? ";
			try {
				return jdbcTemplate.query(query, new RestoRowMapper(),  limit, offset);
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
	public List<BusinessHour>  addBusinessHoursToResto(NewBusinessHours businessHours, int restoId) throws DuplicateKeyException {
		log.warn(businessHours.toString());
		try {
			for (BusinessHour businessHour: businessHours.getBusinessHours()){

				LocalTime openHours = convertToLocalTime(businessHour.getOpeningHour());
				LocalTime closingHours = convertToLocalTime(businessHour.getClosingHour());
				String query = "INSERT INTO resto_roulette.business_hour (resto_id, week_day, opening_hour, closing_hour," +
						" is_lunch" +
						")" +
						"VALUES (?, ?, ?, ?, ?)";
				jdbcTemplate.update(query, restoId, businessHour.getWeekDay(), openHours, closingHours, businessHour.isLunch());
			}
		}catch (DuplicateKeyException e){
			log.error(e.getMessage());
			throw e;
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

	public List<BusinessHour> changeBusinessHours(List<UpdateBusinessHours> updateBusinessHoursList, int restoId) throws SQLException {
		String query = "UPDATE business_hour " +
				" SET opening_hour = ?, closing_hour = ? " +
				" WHERE week_day = ? AND resto_id = ? and is_lunch = ?";
		PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query);
		for (UpdateBusinessHours updateBusinessHours: updateBusinessHoursList) {
			preparedStatement.setString(1,updateBusinessHours.getOpeningHour());
			preparedStatement.setString(2,updateBusinessHours.getClosingHour());
			preparedStatement.setInt(3, updateBusinessHours.getWeekDay());
			preparedStatement.setInt(4, restoId);
			preparedStatement.setBoolean(5, updateBusinessHours.isLunch());
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
		return getBusinessHoursByRestoId(restoId);
	}

	public Restaurant updateResto(int restoId, int ownerId , NewRestaurant newRestaurant) {
		String queryRestoInfo = "UPDATE resto_info SET name = ?, address = ? WHERE resto_id = ?";
		String queryResto = "UPDATE resto SET display_name = ?, resto.owner_id = ? WHERE resto_id = ?";
		try {
			jdbcTemplate.update(queryRestoInfo,newRestaurant.getName(),newRestaurant.getAddress(), restoId);
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

	public Set<String> getAllLabels() {
		String query = "SELECT label_name FROM  resto_label";
		try {
			return new HashSet<>(jdbcTemplate.queryForList(query, String.class));
		}catch (EmptyResultDataAccessException e){
			return Collections.emptySet();
		}
	}
	
	public Set<String> getLabelByRestoId(int restoId) {
		String query = "SELECT label_name FROM resto_label " +
				"JOIN resto_roulette.resto_resto_labels ON resto_label.label_id = resto_resto_labels.label_id " +
				"JOIN resto_roulette.resto r on resto_resto_labels.resto_id = r.resto_id "+
				"WHERE r.resto_id = ?";
		try {
			return new HashSet<>(jdbcTemplate.queryForList(query, String.class, restoId));
		}catch (EmptyResultDataAccessException e){
			return Collections.emptySet();
		}
	}

	public Set<String> addLabelsToResto(int restoId, Set<String> labels) {
		try {
			for (String label: labels){
				String query = "INSERT INTO resto_resto_labels (resto_id,label_id) " +
						"VALUES (?, (SELECT label_id FROM resto_label WHERE label_name = ?))";
				jdbcTemplate.update(query, restoId, label);
			}
		}catch (Exception e){
			log.error(e.getMessage());
			throw new RuntimeException("Error while adding labels to resto");
		}
		Set<String> restoLabels = getLabelByRestoId(restoId);
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
				" resto_roulette.resto.owner_id = ? " +
				" AND resto_roulette.resto.is_deleted = false";
		List<Restaurant> restos = jdbcTemplate.query(query, new RestoRowMapper(), accountId);
		log.warn(restos.toString());
		return restos;
	}

	public void saveRestoMedia(int restoId, String uuid, MediaType type) {
		try {
			String query = "INSERT INTO resto_resto_medias (resto_id,resource_id, media_type_id) " +
					"VALUES (?, ?, ?)";
			jdbcTemplate.update(query, restoId, uuid,type.typeId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}


	public List<MediaResource> getRestoPictureByRestoId(String id) {
		String query = "SELECT resource_id, media_type_id " +
				" FROM resto_roulette.resto_resto_medias m " +
				" WHERE m.resto_id = ? ";
		try {
			return jdbcTemplate.query(query, new MediaMapper(), id);
		}catch (EmptyResultDataAccessException e){
			return Collections.emptyList();
		}
	}

	public void deleteResto(String id) throws RestoNotFoundException {
		String query = "UPDATE resto SET resto.is_deleted = true WHERE resto_id = ?";
		int row = jdbcTemplate.update(query, id);
		if (row == 0) {
			throw new RestoNotFoundException("resto not found cant delete");
		}
	}

	public void updateLabelsResto(int restoId, Set<String> updateLabels) throws SQLException {
		Set<String> originalLabels = getLabelByRestoId(restoId);
		Set<String> labelsToDelete = computeToDelete(originalLabels,updateLabels);
		Set<String> labelsToAdd = computeToAdd(originalLabels,updateLabels);
		removeLabelFromResto(restoId, labelsToDelete);
		this.addLabelsToResto(restoId, labelsToAdd);
	}

	private void removeLabelFromResto(int restoId, Set<String> labelsToDelete) throws SQLException {
		String query = "DELETE t FROM resto_resto_labels t" +
				" JOIN resto_label rt ON rt.label_id = t.label_id " +
				" WHERE rt.label_name = ?" +
				" AND t.resto_id = ?";
		PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query);
		for (String label : labelsToDelete) {
			preparedStatement.setString(1, label);
			preparedStatement.setInt(2, restoId);
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
	}

	private Set<String> computeToDelete(Set<String> originalSet, Set<String> newValuesSet) {
		return originalSet.stream()
				.filter(e -> !newValuesSet.contains(e))
				.collect(Collectors.toSet());
	}

	private Set<String> computeToAdd(Set<String> originalSet, Set<String> newValuesSet) {
		return newValuesSet.stream()
				.filter(e -> !originalSet.contains(e))
				.collect(Collectors.toSet());
	}


	public void updatedRestoFoodTypes(int restoId, Set<String> foodTypeToAdd) throws SQLException {
		Set<String> originalLabels = getLabelByRestoId(restoId);
		Set<String> oldFoodType = computeToDelete(originalLabels,foodTypeToAdd);
		Set<String> newFoodType = computeToAdd(originalLabels,foodTypeToAdd);
		removeRestoFoodTypes(restoId, oldFoodType);
		this.linkFoodsType(restoId, newFoodType);
	}

	private void removeRestoFoodTypes(int restoId, Set<String> removeFoodTypes) throws SQLException {
		String query = "DELETE t FROM resto_resto_types t" +
				" JOIN resto_type rt ON rt.id = t.type_id " +
				" WHERE rt.food_type = ?" +
				" AND t.resto_id = ?";
		PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query);
		for (String foodType : removeFoodTypes) {
			preparedStatement.setString(1, foodType);
			preparedStatement.setInt(2, restoId);
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
	}

}