package com.roulette.resto.business.administration.services;

import com.roulette.resto.business.administration.dto.UserRegistrationHistory;
import com.roulette.resto.business.administration.repository.KpiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KpiService {

	private final KpiRepository kpiRepository;

	public KpiService(KpiRepository kpiRepository) {
		this.kpiRepository = kpiRepository;
	}

	public long[] getUsersRegistration(String year) {
		List<UserRegistrationHistory> historic = kpiRepository.getNewUsersByYear(year);
		long[] result = new long[historic.size()];
		for(int i = 0; i < historic.size(); i++) {
			result[i] = historic.get(i).getTotal();
		}
		return result;
	}
}
