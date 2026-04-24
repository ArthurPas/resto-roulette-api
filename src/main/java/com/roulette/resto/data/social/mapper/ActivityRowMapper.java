package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.RouletteSessionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class ActivityRowMapper implements RowMapper<ActivityDto> {
	@Override
	public ActivityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ActivityDto activityDto = new ActivityDto();
		activityDto.setActivityId(rs.getInt("activity_id"));
		activityDto.setAccountId(rs.getInt("account_id"));
		activityDto.setDescription(rs.getString("description"));
		RouletteSessionDto rouletteSessionDto = new RouletteSessionDto();
		rouletteSessionDto.setSessionId(rs.getString("session_id"));
		rouletteSessionDto.setRestoId(rs.getInt("resto_id"));
		try {
			rs.findColumn("participantsId");
			if(rs.getString("participantsId") != null) {
				String participantsRaw = rs.getString("participantsId");
				if (participantsRaw != null) {
					List<Integer> participantIds = Arrays.stream(participantsRaw.split(","))
							.map(String::trim)
							.map(Integer::parseInt)
							.toList();
					rouletteSessionDto.setParticipantIds(new HashSet<>(participantIds));
				}
			}
		}catch (SQLException e){
			log.info("no participantsId");
		}
		activityDto.setDetails(rouletteSessionDto);
		activityDto.setUploaded(rs.getBoolean("is_uploaded"));
		activityDto.setActivityDate(rs.getDate("activity_date"));
		return activityDto;
	}
}
