package com.roulette.resto.common.entity.mappers;

import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.resto.entity.BusinessHour;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MediaMapper implements RowMapper<MediaResource> {
	@Override
	public MediaResource mapRow(ResultSet rs, int rowNum) throws SQLException {
		MediaResource mediaResource = new MediaResource();
		mediaResource.setResourceId(rs.getString("resource_id"));
		mediaResource.setMediaType(MediaType.valueOf(rs.getString("media_type_id")));
		return mediaResource;
	}
}
