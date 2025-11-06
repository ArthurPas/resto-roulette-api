package com.roulette.resto.resto.entity.mapper;

import com.roulette.resto.resto.entity.BusinessHour;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
@Slf4j
public class BusinessHoursRowMapper implements RowMapper<BusinessHour> {

	@Override
	public BusinessHour mapRow(ResultSet rs, int rowNum) throws SQLException {
		BusinessHour businessHour = new BusinessHour();
		businessHour.setOpeningHour(rs.getTime("opening_hour"));
		businessHour.setClosingHour(rs.getTime("closing_hour"));
		businessHour.setWeekDay(rs.getInt("week_day"));
		return businessHour;
	}
}
