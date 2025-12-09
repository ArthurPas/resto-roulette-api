package com.roulette.resto.service.common;

import com.roulette.resto.dao.common.MediaDao;
import com.roulette.resto.data.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

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
}
