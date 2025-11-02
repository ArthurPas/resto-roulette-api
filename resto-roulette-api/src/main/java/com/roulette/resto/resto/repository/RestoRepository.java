package com.roulette.resto.resto.repository;

import com.roulette.resto.resto.dao.RestoDao;
import com.roulette.resto.resto.entity.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Slf4j
public class RestoRepository {


	private final RestoDao restoDao;

	public RestoRepository(RestoDao restoDao, RestoDao restoDao1) {
		this.restoDao = restoDao1;
	}

	public Date createResto(Restaurant restaurant) {
		return restoDao.createResto(restaurant);
	}
}
