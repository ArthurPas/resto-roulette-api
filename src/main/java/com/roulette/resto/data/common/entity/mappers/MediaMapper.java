package com.roulette.resto.data.common.entity.mappers;

import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class MediaMapper implements RowMapper<MediaResource> {
	@Override
	public MediaResource mapRow(ResultSet rs, int rowNum) throws SQLException {
		MediaResource mediaResource = new MediaResource();
		mediaResource.setResourceId(rs.getString("resource_id"));
		mediaResource.setMediaType(MediaType.fromValue(rs.getInt("media_type_id")));
		return mediaResource;
	}
}
