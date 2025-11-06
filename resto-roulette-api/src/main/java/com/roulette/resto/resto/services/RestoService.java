package com.roulette.resto.resto.services;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dto.in.NewBusinessHours;
import com.roulette.resto.resto.dto.in.NewFoodType;
import com.roulette.resto.resto.dto.in.NewRestaurant;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.repository.RestoRepository;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
			log.warn("yooooo"+newRestaurant.getFoodTypes());
			List<String> existingFoodtype = existingFoodTypesList(newRestaurant.getFoodTypes());
			log.warn("yaaaa"+existingFoodtype.toString());
			restaurant.setFoodType(existingFoodtype);
			restaurant.setOwner(accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()));
		}catch (AccountNotFoundException e) {
			throw new APIError(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		try {
			int restoId = restoRepository.createResto(restaurant);
			restaurant.setId(restoId);
			return restaurant;
		}catch (Exception e) {
			throw new APIError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Restaurant getRestoById(String id) throws APIError {
		try {
			return restoRepository.getRestoById(id);
		}catch (RestoNotFoundException e){
			log.error(e.getMessage());
			throw new APIError(e.getMessage(), HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			throw new APIError("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<String> foodTypeList() throws APIError {
		try {
			return restoRepository.getFoodTypes();
		}catch (Exception e){
			throw new APIError(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public boolean existByFoodType(String foodType) throws APIError {
		return !restoRepository.getFoodTypeByName(foodType.toUpperCase().trim()).isEmpty();
	}

	public List<String> getFoodTypes(String foodType) throws APIError {
		try {
			return restoRepository.getFoodTypeByName(foodType.toUpperCase().trim());
		}catch (Exception e){
			throw new APIError(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public List<String> existingFoodTypesList(List<String> foodTypes) {
		List<String> validFootType = restoRepository.getFoodTypes();
		List<String> newRestofoodType = new ArrayList<>();
		log.warn("coucou {}", foodTypes);
		foodTypes.forEach(foodType -> {if (validFootType.contains(foodType)) {newRestofoodType.add(foodType);}});
		return newRestofoodType;
	}

	public NewFoodType createFoodType(NewFoodType newFoodType) throws APIError {
		if(existByFoodType(newFoodType.getFoodType())) {
				throw new APIError("This type already exists", HttpStatus.BAD_REQUEST);
		}
		try {
			String cleanedFoodType = newFoodType.getFoodType().toUpperCase().trim()
					.replaceAll("é","e")
					.replaceAll("è","e")
					.replaceAll("[^a-zA" +"-Z]","");
			restoRepository.createNewFoodType(cleanedFoodType);
			return new NewFoodType(cleanedFoodType);
		}catch (Exception e) {
			throw new APIError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<Restaurant> getRestos(int offset) throws APIError {
		try {
			int nbResult = 25;
			return new ArrayList<>(restoRepository.getAllRestos(nbResult, offset * nbResult));
		}catch (Exception e) {
			throw new APIError("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<BusinessHour> addBusinessHoursToResto(NewBusinessHours businessHours) {
		try{
			return restoRepository.addBusinessHoursToResto(businessHours);
		}catch (Exception e){
			throw e;
		}
	}
}
