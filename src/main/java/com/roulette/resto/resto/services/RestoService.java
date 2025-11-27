package com.roulette.resto.resto.services;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dto.MenuPicture;
import com.roulette.resto.resto.dto.in.*;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Label;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.repository.RestoRepository;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RestoService {
	private final AccountRepository accountRepository;
	private final RestoRepository restoRepository;

	public RestoService(AccountRepository accountRepository, RestoRepository restoRepository) {
		this.accountRepository = accountRepository;
		this.restoRepository = restoRepository;
	}

	public Restaurant createResto(NewRestaurant newRestaurant) throws APIError {
		Restaurant restaurant = new Restaurant();
		restaurant.setAddress(newRestaurant.getAddress());
		restaurant.setLatitude(BigDecimal.valueOf(newRestaurant.getLatitude()));
		restaurant.setLongitude(BigDecimal.valueOf(newRestaurant.getLongitude()));
		restaurant.setName(newRestaurant.getName());
		restaurant.setName(newRestaurant.getName());
		restaurant.setDisplayName(newRestaurant.getDisplayName());

		try{
			//Remove from newRestaurant payload food type that not exists in db
			List<String> existingFoodtype = existingFoodTypesList(newRestaurant.getFoodTypes());
			restaurant.setFoodTypes(existingFoodtype);
			restaurant.setOwner(accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()));
		}catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
		try {
			int restoId = restoRepository.createResto(restaurant);
			restaurant.setId(restoId);
			return restaurant;
		}catch (Exception e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Restaurant getRestoById(String id) throws APIError {
		try {
			return restoRepository.getRestoById(id);
		}catch (RestoNotFoundException e){
			log.error(e.getMessage());
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error(e.getMessage());
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<String> foodTypeList() throws APIError {
		try {
			return restoRepository.getFoodTypes();
		}catch (Exception e){
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean existByFoodType(String foodType) throws APIError {
		return !restoRepository.getFoodTypeByName(foodType.toUpperCase().trim()).isEmpty();
	}

	public List<String> existingFoodTypesList(List<String> foodTypes) {
		List<String> validFootType = restoRepository.getFoodTypes();
		List<String> newRestofoodType = new ArrayList<>();
		foodTypes.forEach(foodType -> {if (validFootType.contains(foodType)) {newRestofoodType.add(foodType);}});
		return newRestofoodType;
	}

	public NewFoodType createFoodType(NewFoodType newFoodType) throws APIError {
		if(existByFoodType(newFoodType.getFoodType())) {
				throw new APIError(80, HttpStatus.BAD_REQUEST);
		}
		try {
			String cleanedFoodType = newFoodType.getFoodType().toUpperCase().trim()
					.replaceAll("é","e")
					.replaceAll("è","e")
					.replaceAll("[^a-zA" +"-Z]","");
			restoRepository.createNewFoodType(cleanedFoodType);
			return new NewFoodType(cleanedFoodType);
		}catch (Exception e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<Restaurant> getRestos(int offset) throws APIError {
		try {
			int nbResult = 25;
			return new ArrayList<>(restoRepository.getAllRestos(nbResult, offset * nbResult));
		}catch (RestoNotFoundException e) {
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error(e.getMessage());
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<BusinessHour> addBusinessHoursToResto(NewBusinessHours businessHours, String id) {
		try{
			return restoRepository.addBusinessHoursToResto(businessHours, id);
		}catch (Exception e){
			throw e;
		}
	}

	public List<BusinessHour> updateBusinessHours(UpdateBusinessHours newBusinessHours, String id) {
		try{
			return restoRepository.changeBusinessHours(newBusinessHours, id);
		}catch (Exception e){
			throw e;
		}

	}

	public Restaurant updateRestoInfoById(String id, NewRestaurant newRestaurant) throws APIError {
		try {
			List<String> existingFoodTypesList = existingFoodTypesList(newRestaurant.getFoodTypes());
			newRestaurant.setFoodTypes(existingFoodTypesList);
			return restoRepository.updateRestoById(id, newRestaurant);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		} catch (RestoNotFoundException e) {
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}
	}

	public Label addNewLabel(Label label) throws APIError {
		try {
			return new Label(restoRepository.newLabel(label));
		}catch (Exception e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Map<String, List<String>> addLabelsResto(AddLabels labels, String id) throws APIError {
		try {

			List<String> labelsNames = restoRepository.addLabelsToResto(labels, id);
			Map<String, List<String>> labelsResto = new HashMap<>();
			labelsResto.put("labels", labelsNames);
			return labelsResto;
		}catch (RuntimeException e){
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<String> getLabels() {
		return restoRepository.getLabels();
	}

	public List<Restaurant> getRestosByOwnerId(int accountId) {
		return restoRepository.getRestoByOwner(accountId);
	}

	public String addMenuPicture(String restoId, MultipartFile menuPicture) throws APIError {
		try{
			return restoRepository.saveMenuPicture(Integer.parseInt(restoId),menuPicture.getBytes());
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError();
		}
	}

	public List<MenuPicture> getMenusByRestoId(String id) {
		return restoRepository.getRestoPictureByRestoId(id);
	}
}
