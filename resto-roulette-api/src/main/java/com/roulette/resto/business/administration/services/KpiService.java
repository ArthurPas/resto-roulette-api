package com.roulette.resto.business.administration.services;

import com.roulette.resto.business.administration.dto.UserRegistrationHistory;
import com.roulette.resto.business.administration.dto.out.TrendDto;
import com.roulette.resto.business.administration.dto.out.VARIATION;
import com.roulette.resto.business.administration.repository.KpiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@Slf4j
public class KpiService {

	private final KpiRepository kpiRepository;

	public KpiService(KpiRepository kpiRepository) {
		this.kpiRepository = kpiRepository;
	}

	public long[] getUsersRegistrationHistoric(String year) {
		List<UserRegistrationHistory> historic = kpiRepository.getNewUsersByYear(year);
		long[] result = new long[historic.size()];
		for(int i = 0; i < historic.size(); i++) {
			result[i] = historic.get(i).getTotal();
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
		trendDto.setPercentageVariation(variation);
		if(variation > 0) {
			trendDto.setVariationType(VARIATION.UP);
		} else if(variation < 0) {
			trendDto.setVariationType(VARIATION.DOWN);
		} else trendDto.setVariationType(VARIATION.EQUAL);
		return trendDto;
	}

	public TrendDto getWheelTrend() {
		TrendDto trendDto = new TrendDto();
		trendDto.setTotal(kpiRepository.getWheelLaunched());
		int currentMonth = YearMonth.now().getMonthValue();
		int lastMonth = YearMonth.now().minusMonths(1).getMonthValue();
		int currentYear = YearMonth.now().getYear();
		Float variation = kpiRepository.getWheelTrend(currentMonth, lastMonth, currentYear );
		trendDto.setPercentageVariation(variation);
		if(variation > 0) {
			trendDto.setVariationType(VARIATION.UP);
		} else if(variation < 0) {
			trendDto.setVariationType(VARIATION.DOWN);
		} else trendDto.setVariationType(VARIATION.EQUAL);
		return trendDto;
	}
}
