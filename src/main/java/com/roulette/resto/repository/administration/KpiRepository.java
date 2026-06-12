package com.roulette.resto.repository.administration;

import com.roulette.resto.dao.administration.KpiDao;
import com.roulette.resto.data.administration.dto.UserRegistrationHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class KpiRepository {
	final KpiDao kpiDao;
	public KpiRepository(KpiDao kpiDao) {
		this.kpiDao = kpiDao;
	}

	public List<UserRegistrationHistory> getNewUsersByYear(String year) {
		return kpiDao.getNewUsersByYear(year);
	}

	public long getNbUsers() {
		return kpiDao.getNbUsers();
	}

	public Float getRegistrationTrend(int currentMonth, int comparedMonth, int year) {
		return kpiDao.getRegistrationTrend(currentMonth, comparedMonth, year);
	}

	public Float getWheelTrend(int currentMonth, int comparedMonth, int year) {
//		log.info("Getting wheel trend for current month: " + currentMonth);
		return kpiDao.getWheelTrend(currentMonth, comparedMonth, year);
	}
}
