package com.roulette.resto.business.administration.services;

import com.roulette.resto.business.administration.dto.UserRegistrationHistory;
import com.roulette.resto.business.administration.dto.out.GlobalUserStatDto;
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

	public GlobalUserStatDto getVariationRegistration() {
		GlobalUserStatDto globalUserStatDto = new GlobalUserStatDto();
		globalUserStatDto.setTotalRegistered(kpiRepository.getNbUsers());
		int currentMonth = YearMonth.now().getMonthValue();
		int lastMonth = YearMonth.now().minusMonths(1).getMonthValue();
		Float variation = kpiRepository.getRegistrationVariation(currentMonth, lastMonth);
		globalUserStatDto.setVariation(variation);
		if(variation > 0) {
			globalUserStatDto.setVariationType(VARIATION.UP);
		} else if(variation < 0) {
			globalUserStatDto.setVariationType(VARIATION.DOWN);
		} else globalUserStatDto.setVariationType(VARIATION.EQUAL);
		return globalUserStatDto;
	}
}
