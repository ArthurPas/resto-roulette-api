package com.roulette.resto.resto.services;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.resto.dto.in.NewRestaurant;
import com.roulette.resto.resto.entity.Food;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.repository.RestoRepository;
import com.roulette.resto.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
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
		restaurant.setLatitude(newRestaurant.getLatitude());
		restaurant.setLongitude(newRestaurant.getLongitude());
		restaurant.setName(newRestaurant.getName());
		restaurant.setName(newRestaurant.getName());
		restaurant.setDisplayName(newRestaurant.getDisplayName());
		List<Food> foods = new ArrayList<>();
		for (String food : newRestaurant.getFoodTypes())
			try {
				foods.add(Food.fromValue(food));
			}catch (Exception e) {
				log.error(e.getMessage());
				throw new APIError(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		try{
			restaurant.setFoodType(foods);
			restaurant.setOwner(accountRepository.getAccountByLogin(newRestaurant.getLoginOwner()));
		}catch (AccountNotFoundException e) {
			throw new APIError(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		try {
			restoRepository.createResto(restaurant);
			return restaurant;
		}catch (Exception e) {
			throw new APIError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
