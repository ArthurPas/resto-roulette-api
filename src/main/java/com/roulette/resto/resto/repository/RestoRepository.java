package com.roulette.resto.resto.repository;

import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.common.service.MediaService;
import com.roulette.resto.resto.dao.RestoDao;
import com.roulette.resto.resto.dto.in.*;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Label;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import javax.security.auth.login.AccountNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class RestoRepository {


	private final RestoDao restoDao;
	private final AccountRepository accountRepository;
	private final MediaService mediaService;

	public RestoRepository(RestoDao restoDao, RestoDao restoDao1, AccountRepository accountRepository, MediaService mediaService) {
		this.restoDao = restoDao1;
		this.accountRepository = accountRepository;
		this.mediaService = mediaService;
	}

	public int createResto(Restaurant restaurant) {
		try {
			return restoDao.createResto(restaurant);
		}catch (Exception e) {
			log.error(e.getMessage());
			throw  e;
		}
	}

	public Restaurant getRestoById(String id) throws RestoNotFoundException {
		try {

			Restaurant restaurant = restoDao.getRestoById(Integer.parseInt(id));
			restaurant.setBusinessHours(restoDao.getBusinessHoursByRestoId(Integer.parseInt(id)));
			restaurant.setMedias(this.getRestoPictureByRestoId(id));
			return restaurant;
		}catch (RestoNotFoundException e){
			log.error(e.getMessage());
			throw e;
		}
	}

	public void createNewFoodType(String foodType) {
		restoDao.createFoodType(foodType);
	}

	public List<String> getFoodTypes() {
		return restoDao.getFoodTypes();
	}

	public List<String> getFoodTypeByName(String foodType) {
		return restoDao.getFoodType(foodType);
	}

	public List<Restaurant> getAllRestos(int limit, int offset) throws RestoNotFoundException {
		try {
			List<Restaurant> restaurants = restoDao.getAllRestos(limit, offset);
			for(Restaurant restaurant : restaurants) {
				List<MediaResource> mediaResources = this.getRestoPictureByRestoId(String.valueOf(restaurant.getId()));
				restaurant.setMedias(mediaResources);
			}
			return restaurants;
		} catch (RestoNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<BusinessHour> addBusinessHoursToResto(NewBusinessHours businessHours, String id) {
		return restoDao.addBusinessHoursToResto(businessHours, Integer.parseInt(id));
	}

	public List<BusinessHour> changeBusinessHours(List<UpdateBusinessHours> newBusinessHours, String id) throws SQLException {
		return restoDao.changeBusinessHours(newBusinessHours, Integer.parseInt(id));
	}

	public Restaurant updateRestoById(String id, NewRestaurant newRestaurant) throws AccountNotFoundException, RestoNotFoundException {
		int ownerId = accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()).getAccountId();
		restoDao.addNewFootypes(Integer.parseInt(id),newRestaurant.getFoodTypes());
		return restoDao.updateResto(Integer.parseInt(id),ownerId, newRestaurant);
	}

	public String newLabel(Label label) {
		if(restoDao.labelExist(label.getLabelName())>0){
			throw new RuntimeException("Label already exists");
		};
		return restoDao.newLabel(label.getLabelName());
	}

	public List<String> addLabelsToResto(AddLabels labels, String id) {
		List<String> labelsToAdd = getOnlyExistingLabels(labels.getLabels());
		log.warn("Labels to add "+labelsToAdd);
		try {
			return restoDao.addLabelsToResto(Integer.parseInt(id),labelsToAdd);
		}catch (RuntimeException e) {
			log.error(e.getMessage());
			throw  e;
		}
	}
	private List<String> getOnlyExistingLabels(List<String> labels){
		List<String> result = new ArrayList<>();
		List<String> existingLabels = restoDao.getAllLabels();
		for (String label : labels) {
			if (existingLabels.contains(label)) {
				result.add(label);
			}
		}
		return result;
	}
	private List<String> getOnlyExistingFoodType(List<String> labels){
		List<String> result = new ArrayList<>();
		List<String> existingLabels = restoDao.getAllLabels();
		for (String label : labels) {
			if (existingLabels.contains(label)) {
				result.add(label);
			}
		}
		return result;
	}

	public List<String> getLabels() {
		return restoDao.getAllLabels();
	}

	public List<Restaurant> getRestoByOwner(int accountId) {
		return restoDao.getRestoByOwner(accountId);
	}

	public String savePicture(int restoId, MediaType mediaType, byte[] bytes) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		try {
			BufferedImage newImage = ImageIO.read(byteArrayInputStream);
			String uuid = mediaService.saveImage(newImage, mediaType);
			restoDao.saveRestoMedia(restoId, uuid, mediaType);
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<MediaResource> getRestoPictureByRestoId(String id) {
		return restoDao.getRestoPictureByRestoId(id);
	}

	public void deleteResto(String id) throws RestoNotFoundException {
		try {
			restoDao.deleteResto(id);
		}catch (RestoNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
