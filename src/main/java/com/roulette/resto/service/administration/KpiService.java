package com.roulette.resto.service.administration;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.dao.administration.KpiDao;
import com.roulette.resto.data.administration.dto.UserRegistrationHistory;
import com.roulette.resto.data.administration.dto.out.TrendDto;
import com.roulette.resto.data.administration.dto.out.Variation;
import com.roulette.resto.repository.administration.KpiRepository;
import com.roulette.resto.service.social.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class KpiService {

	private final KpiRepository kpiRepository;
	private final KpiDao kpiDao;

	public KpiService(KpiRepository kpiRepository, KpiDao kpiDao) {
		this.kpiRepository = kpiRepository;
		this.kpiDao = kpiDao;
	}

	public List<Long> getUsersRegistrationHistoric(String year) {
		List<UserRegistrationHistory> historic = kpiRepository.getNewUsersByYear(year);
		List<Long> result = new ArrayList<>();
		if (historic != null) {
			for (UserRegistrationHistory historicUser : historic) {
				result.add(historicUser.getTotal());
			}
		}
		return result;
	}
	public TrendDto getRegistrationTrend() {
		Long total = kpiRepository.getNbUsers();
		YearMonth now = YearMonth.now();
		int currentMonth = now.getMonthValue();
		int lastMonth = now.minusMonths(1).getMonthValue();
		int currentYear = now.getYear();

		Float variation = kpiRepository.getRegistrationTrend(currentMonth, lastMonth, currentYear);

		return buildTrendDto(total != null ? total : 0L, variation);
	}
	public TrendDto getWheelTrend() {
		Long total = kpiDao.getWheelLaunched();

		YearMonth now = YearMonth.now();
		int currentMonth = now.getMonthValue();
		int lastMonth = now.minusMonths(1).getMonthValue();
		int currentYear = now.getYear();

		Float variation = kpiRepository.getWheelTrend(currentMonth, lastMonth, currentYear);

		return buildTrendDto(total != null ? total : 0L, variation);
	}
	private TrendDto buildTrendDto(Long total, Float variation) {
		TrendDto trendDto = new TrendDto();
		trendDto.setTotal(total);
		if (variation == null) {
			trendDto.setPercentageVariation(0f);
			trendDto.setVariationType(Variation.EQUAL);
			return trendDto;
		}
		trendDto.setPercentageVariation(Math.abs(variation));
		if (variation > 0) {
			trendDto.setVariationType(Variation.UP);
		} else if (variation < 0) {
			trendDto.setVariationType(Variation.DOWN);
		} else {
			trendDto.setVariationType(Variation.EQUAL);
		}

		return trendDto;
	}
}