package com.roulette.resto.resto.repository;

import com.roulette.resto.common.exception.RestoNotFoundException;
import com.roulette.resto.resto.dao.RestoDao;
import com.roulette.resto.resto.dto.in.AddLabels;
import com.roulette.resto.resto.dto.in.NewBusinessHours;
import com.roulette.resto.resto.dto.in.NewRestaurant;
import com.roulette.resto.resto.dto.in.UpdateBusinessHours;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Label;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class RestoRepository {


	private final RestoDao restoDao;
	private final AccountRepository accountRepository;

	public RestoRepository(RestoDao restoDao, RestoDao restoDao1, AccountRepository accountRepository) {
		this.restoDao = restoDao1;
		this.accountRepository = accountRepository;
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
		Restaurant restaurant = restoDao.getRestoById(Integer.parseInt(id));
		restaurant.setBusinessHours(restoDao.getBusinessHoursByRestoId(Integer.parseInt(id)));
		return restaurant;
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

	public List<Restaurant> getAllRestos(int limit, int offset) {
		return restoDao.getAllRestos(limit, offset);
	}

	public List<BusinessHour> addBusinessHoursToResto(NewBusinessHours businessHours) {
		return restoDao.addBusinessHoursToResto(businessHours);
	}

	public List<BusinessHour> changeBusinessHours(UpdateBusinessHours newBusinessHours) {
		return restoDao.changeBusinessHours(newBusinessHours);
	}

	public Restaurant updateRestoById(String id, NewRestaurant newRestaurant) throws AccountNotFoundException {
		int ownerId = accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()).getAccountId();
		return restoDao.updateResto(Integer.parseInt(id),ownerId, newRestaurant);
	}

	public String newLabel(Label label) {
		if(restoDao.labelExist(label.getLabelName())>0){
			throw new RuntimeException("Label already exists");
		};
		return restoDao.newLabel(label.getLabelName());
	}

	public List<String> addLabelsToResto(AddLabels labels) {
		List<String> labelsToAdd = getOnlyExistingLabels(labels.getLabels());
		log.warn("Labels to add "+labelsToAdd);
		try {
			return restoDao.addLabelsToResto(Integer.parseInt(labels.getRestoId()),labelsToAdd);
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

	public List<String> getLabels() {
		return restoDao.getAllLabels();
	}
}
