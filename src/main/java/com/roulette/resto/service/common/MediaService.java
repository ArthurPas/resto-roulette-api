package com.roulette.resto.service.common;

import com.roulette.resto.dao.common.MediaDao;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {

	private static String baseUrl;

	@Value("${app.storage.images.maxResolution.avatar}")
	private int maxResolutionAvatar;

	@Value("${app.storage.images.maxResolution.menu}")
	private int maxResolutionMenu;

	@Value("${app.storage.images.maxResolution.logo}")
	private int maxResolutionLogo;

	private final MediaDao mediaDao;

	public MediaService(MediaDao mediaDao) {
		this.mediaDao = mediaDao;
	}

	@Value("${app.base-url}")
	public void setBaseUrl(String url) {
		MediaService.baseUrl = url;
	}

	public String saveImage(BufferedImage bufferedImage, MediaType mediaType) throws IOException {
		return switch (mediaType) {
			case MENU, RESTO -> mediaDao.saveMedia(bufferedImage, maxResolutionMenu);
			case AVATAR -> mediaDao.saveSquaredMedia(bufferedImage, maxResolutionAvatar);
			case LOGO -> mediaDao.saveSquaredMedia(bufferedImage, maxResolutionLogo);
		};
	}

	public void removeFile(String uuid) throws IOException {
		mediaDao.removeMedia(uuid);
	}

	public static List<MediaResponse> buildMediaUrl(List<MediaResource> pictures) {
		if (pictures != null && !pictures.isEmpty()) {
			return pictures.stream()
					.map(pic -> {
						String downloadUrl = buildMediaUrl(pic.getResourceId());
						return new MediaResponse(pic.getMediaType().toString(), downloadUrl);
					})
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public static String buildMediaUrl(String resourceId) {
		if (resourceId == null || resourceId.isEmpty()) {
			return null;
		}
		return baseUrl + "/medias/" + resourceId;
	}
}