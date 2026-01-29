package com.roulette.resto.service.resto;

import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import com.roulette.resto.data.resto.dto.in.NewBusinessHours;
import com.roulette.resto.data.resto.dto.in.NewFoodType;
import com.roulette.resto.data.resto.dto.in.NewRestaurant;
import com.roulette.resto.data.resto.dto.in.UpdateBusinessHours;
import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.resto.entity.BusinessHour;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.exception.RestoNotFoundException;
import com.roulette.resto.repository.resto.RestoRepository;
import com.roulette.resto.repository.social.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
		restaurant.setName(newRestaurant.getName());
		restaurant.setName(newRestaurant.getName());
		restaurant.setDisplayName(newRestaurant.getDisplayName());
		restaurant.setLabels(newRestaurant.getLabels());
		try{
			Set<String> existingFoodtype = existingFoodTypesList(newRestaurant.getFoodTypes());
			restaurant.setFoodTypes(existingFoodtype);
			if(newRestaurant.getLoginOwner() != null && !newRestaurant.getLoginOwner().isEmpty()){
				restaurant.setOwner(accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()));
			}
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

	public RestoDto getRestoById(String id) throws APIError {
		try {
			return new RestoDto(restoRepository.getRestoById(id));
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

	public boolean existByFoodType(String foodType) {
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

	public List<RestoDto> getRestos(int offset) throws APIError {
		try {
			int nbResult = 25;
			List<Restaurant> restaurants = new ArrayList<>(restoRepository.getAllRestos(nbResult, offset * nbResult));
			return restaurants.stream().map(RestoDto::new).toList();
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
		if(newRestaurant.getFoodTypes() == null || newRestaurant.getFoodTypes().isEmpty() || newRestaurant.getFoodTypes().contains(null)) {
			throw new APIError(8000, HttpStatus.BAD_REQUEST);
		}
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


	public Set<String> getLabels() {
		return restoRepository.getLabels();
	}

	public List<Restaurant> getRestosByOwnerId(int accountId) throws APIError {
		return restoRepository.getRestoByOwner(accountId);
	}

	private MediaResource addMenuPicture(String restoId, MultipartFile menuPicture) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(restoId), MediaType.MENU,
					menuPicture.getBytes());
			return new MediaResource(resourceId,MediaType.MENU);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR );
		}
	}
	private MediaResource addLogoPicture(String id, MultipartFile logo) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(id), MediaType.LOGO, logo.getBytes());
			return new MediaResource(resourceId,MediaType.LOGO);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private MediaResource addRestoPicture(String id, MultipartFile photo) throws APIError {
		try{
			String resourceId = restoRepository.savePicture(Integer.parseInt(id), MediaType.RESTO, photo.getBytes());
			return new MediaResource(resourceId,MediaType.RESTO);
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<MediaResource> getPictures(String id) {
		try {

			return restoRepository.getRestoPictureByRestoId(id);
		}catch (RestoNotFoundException ex){
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}
	}

	public void deleteResto(String id) throws APIError {
		try {
			restoRepository.deleteResto(id);
		}catch (RestoNotFoundException e){
			log.error(e.getMessage());
			throw new APIError(84, HttpStatus.NOT_FOUND);
		}
	}

	public MediaResource addPicture(String id, MultipartFile menuPicture, String pictureType) throws APIError {
		return switch (pictureType.toLowerCase().trim()) {
			case "menu" -> addMenuPicture(id, menuPicture);
			case "logo" -> addLogoPicture(id, menuPicture);
			case "resto" -> addRestoPicture(id, menuPicture);
			default -> addRestoPicture(id, menuPicture);
		};
	}

	public void removeMedia(String uuid) {
		try {
			restoRepository.removeRestoPicture(uuid);
		}catch (RestoNotFoundException ex){
			throw new APIError(84, HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Restaurant> getRestosBasicInfoByIds(Set<Integer> ids) {
		if(ids.isEmpty()){
			return Collections.emptyList();
		}
		return restoRepository.getRestosBasicInfoByIds(ids);
	}

	public void verifyResto(String id){
		restoRepository.verifyResto(id);
	}

	public void unverifyResto(String id) {
		restoRepository.unVerifyResto(id);
	}

	public void submitVerification(String id){
		restoRepository.submitVerification(id);
	}
	public void rejectVerification(String id){
		restoRepository.rejectVerification(id);
	}
	public boolean isRestoOwnerByAccountId(int restoId, int accountId) {
		return restoRepository.isRestoOwnerByAccountId(restoId, accountId);
	}
}
