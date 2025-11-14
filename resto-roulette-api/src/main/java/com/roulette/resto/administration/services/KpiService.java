package com.roulette.resto.administration.services;

import com.roulette.resto.administration.dao.KpiDao;
import com.roulette.resto.administration.dto.UserRegistrationHistory;
import com.roulette.resto.administration.dto.out.TrendDto;
import com.roulette.resto.administration.dto.out.Variation;
import com.roulette.resto.administration.repository.KpiRepository;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.social.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KpiService {

	private final KpiRepository kpiRepository;

	private final JwtService jwtService;
	private final AccountService accountService;
	private final KpiDao kpiDao;

	public KpiService(KpiRepository kpiRepository, JwtService jwtService, AccountService accountService, KpiDao kpiDao) {
		this.kpiRepository = kpiRepository;
		this.jwtService = jwtService;
		this.accountService = accountService;
		this.kpiDao = kpiDao;
	}

	public List<Long> getUsersRegistrationHistoric(String year) {
		List<UserRegistrationHistory> historic = kpiRepository.getNewUsersByYear(year);
		List<Long> result = new ArrayList<>();
		for (UserRegistrationHistory historicUser : historic) {
			result.add(historicUser.getTotal());
		}
		return result;
	}

	public TrendDto getRegistrationTrend() {
		TrendDto trendDto = new TrendDto();
		trendDto.setTotal(kpiRepository.getNbUsers());
		int currentMonth = YearMonth.now().getMonthValue();
		int lastMonth = YearMonth.now().minusMonths(1).getMonthValue();
		int currentYear = YearMonth.now().getYear();
		Float variation = kpiRepository.getRegistrationTrend(currentMonth, lastMonth, currentYear);
		trendDto.setPercentageVariation(Math.abs(variation));
		if(variation > 0) {
			trendDto.setVariationType(Variation.UP);
		} else if(variation < 0) {
			trendDto.setVariationType(Variation.DOWN);
		} else trendDto.setVariationType(Variation.EQUAL);
		return trendDto;
	}

	public TrendDto getWheelTrend() {
		TrendDto trendDto = new TrendDto();
		trendDto.setTotal(kpiDao.getWheelLaunched());
		int currentMonth = YearMonth.now().getMonthValue();
		int lastMonth = YearMonth.now().minusMonths(1).getMonthValue();
		int currentYear = YearMonth.now().getYear();
		Float variation = kpiRepository.getWheelTrend(currentMonth, lastMonth, currentYear);
		trendDto.setPercentageVariation(Math.abs(variation));
		if(variation > 0) {
			trendDto.setVariationType(Variation.UP);
		} else if(variation < 0) {
			trendDto.setVariationType(Variation.DOWN);
		} else trendDto.setVariationType(Variation.EQUAL);
		return trendDto;
	}
}
