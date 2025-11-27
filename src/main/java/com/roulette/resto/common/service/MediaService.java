package com.roulette.resto.common.service;

import com.roulette.resto.common.dao.MediaDao;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.common.exception.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
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
				case AVATAR -> mediaDao.saveMedia(bufferedImage, maxResolutionAvatar);
				case MENU -> mediaDao.saveMedia(bufferedImage, maxResolutionMenu);
				default -> mediaDao.saveMedia(bufferedImage, maxResolutionLogo);
			};
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
