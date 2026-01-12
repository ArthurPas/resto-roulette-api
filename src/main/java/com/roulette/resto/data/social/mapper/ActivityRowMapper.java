package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.roulette.Activity;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.entity.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityRowMapper implements RowMapper<ActivityDto> {
	@Override
	public ActivityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ActivityDto activityDto = new ActivityDto();
		activityDto.setAccountId(rs.getInt("account_id"));
		activityDto.setDescription(rs.getString("description"));
		activityDto.setRestoId(rs.getInt("resto_id"));
		activityDto.setActivityId(rs.getInt("activity_id"));
		return activityDto;
	}
}
