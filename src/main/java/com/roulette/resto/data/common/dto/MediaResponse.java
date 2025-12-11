package com.roulette.resto.data.common.dto;

import com.roulette.resto.data.common.entity.MediaResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
	String type;
	String url;

	public MediaResponse(MediaResource mediaResource) {
		this.url = mediaResource.getResourceId();
		this.type = mediaResource.getMediaType().toString();
	}
}
