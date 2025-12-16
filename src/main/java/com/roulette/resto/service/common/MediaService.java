package com.roulette.resto.service.common;

import com.roulette.resto.dao.common.MediaDao;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {


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


	public String saveImage(BufferedImage bufferedImage, MediaType mediaType) throws IOException {
		try{
			return switch (mediaType) {
				case MENU, RESTO -> mediaDao.saveMedia(bufferedImage, maxResolutionMenu);
				case AVATAR -> mediaDao.saveSquaredMedia(bufferedImage, maxResolutionAvatar);
				case LOGO -> mediaDao.saveSquaredMedia(bufferedImage, maxResolutionLogo);
			};
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void removeFile(String uuid) throws IOException {
		try {
			mediaDao.removeMedia(uuid);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	public static List<MediaResponse> buildMediaUrl(List<MediaResource> pictures) {
		List<MediaResponse> response = pictures.stream()
				.map(pic -> {
					String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/medias/")
							.path(pic.getResourceId())
							.toUriString();

					return new MediaResponse(pic.getMediaType().toString(),downloadUrl);
				})
				.collect(Collectors.toList());
		return response;
	}
	public static String buildMediaUrl(String resourceId) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/medias/")
				.path(resourceId)
				.toUriString();
	}

}
