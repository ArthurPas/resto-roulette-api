package com.roulette.resto.resto.services;

import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dao.RestoDao;
import com.roulette.resto.resto.dto.in.*;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Label;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.repository.RestoRepository;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class RestoService {
	private final AccountRepository accountRepository;
	private final RestoRepository restoRepository;

	public RestoService(AccountRepository accountRepository, RestoRepository restoRepository, RestoDao restoDao) {
		this.accountRepository = accountRepository;
		this.restoRepository = restoRepository;
	}

	public Restaurant createResto(NewRestaurant newRestaurant) throws APIError {
		Restaurant restaurant = new Restaurant();
		restaurant.setAddress(newRestaurant.getAddress());
		restaurant.setName(newRestaurant.getName());
		restaurant.setName(newRestaurant.getName());
		restaurant.setDisplayName(newRestaurant.getDisplayName());
		restaurant.setLabels(newRestaurant.getLabels());
		try{
			//Remove from newRestaurant payload food type that not exists in db
			Set<String> existingFoodtype = existingFoodTypesList(newRestaurant.getFoodTypes());
			restaurant.setFoodTypes(existingFoodtype);
			restaurant.setOwner(accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()));
		}catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
		try {
			int restoId = restoRepository.createResto(restaurant);
			return restoRepository.getRestoById(String.valueOf(restoId));
		}catch (RestoNotFoundException e) {
			throw new APIError(84, HttpStatus.INTERNAL_SERVER_ERROR);
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

	public Set<String> foodTypeList() throws APIError {
		try {
			return restoRepository.getFoodTypes();
		}catch (Exception e){
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean existByFoodType(String foodType) throws APIError {
		return !restoRepository.getFoodTypeByName(foodType.toUpperCase().trim()).isEmpty();
	}

	public Set<String> existingFoodTypesList(Set<String> foodTypes) {
		Set<String> validFootType = restoRepository.getFoodTypes();
		Set<String> newRestofoodType = new HashSet<>();
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

	public List<BusinessHour> addBusinessHoursToResto(NewBusinessHours businessHours, String id) throws APIError {
		try{
			return restoRepository.addBusinessHoursToResto(businessHours, id);
		}catch (DuplicateKeyException e){
			log.error(e.getMessage());
			throw new APIError(800,HttpStatus.BAD_REQUEST);
		}
	}

	public List<BusinessHour> updateBusinessHours(List<UpdateBusinessHours> newBusinessHours, String id) throws APIError {
		try{
			return restoRepository.changeBusinessHours(newBusinessHours, id);
		}catch (SQLException e){
			log.error(e.getMessage());
			throw new APIError(85, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public Restaurant updateRestoInfoById(String id, NewRestaurant newRestaurant) throws APIError {
		try {
			Set<String> existingFoodTypesList = existingFoodTypesList(newRestaurant.getFoodTypes());
			newRestaurant.setFoodTypes(existingFoodTypesList);
			return restoRepository.updateRestoById(id, newRestaurant);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		} catch (RestoNotFoundException e) {
			throw new APIError(84, HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			throw new APIError(85, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Label addNewLabel(Label label) throws APIError {
		try {
			return new Label(restoRepository.newLabel(label));
		}catch (Exception e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	public Map<String, Set<String>> addLabelsResto(Set<String> labels, int id) throws APIError {
//		try {
//
//			Set<String> labelsNames = restoRepository.addLabelsToResto(labels, id);
//			Map<String, Set<String>> labelsResto = new HashMap<>();
//			labelsResto.put("labels", labelsNames);
//			return labelsResto;
//		}catch (RuntimeException e){
//			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}

	public Set<String> getLabels() {
		return restoRepository.getLabels();
	}

	public List<Restaurant> getRestosByOwnerId(int accountId) {
		return restoRepository.getRestoByOwner(accountId);
	}

	public MediaResource addMenuPicture(String restoId, MultipartFile menuPicture) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(restoId), MediaType.MENU,
					menuPicture.getBytes());
			return new MediaResource(resourceId,MediaType.MENU);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR );
		}
	}
	public MediaResource addLogoPicture(String id, MultipartFile logo) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(id), MediaType.LOGO, logo.getBytes());
			return new MediaResource(resourceId,MediaType.LOGO);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public MediaResource addRestoPicture(String id, MultipartFile photo) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(id), MediaType.RESTO, photo.getBytes());
			return new MediaResource(resourceId,MediaType.RESTO);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<MediaResource> getPictures(String id) {
		return restoRepository.getRestoPictureByRestoId(id);
	}

	public void deleteResto(String id) throws APIError {
		try {
			restoRepository.deleteResto(id);
		}catch (RestoNotFoundException e){
			log.error(e.getMessage());
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}
	}

}
