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


	private final MediaDao mediaDao;

	public MediaService(MediaDao mediaDao) {
		this.mediaDao = mediaDao;
	}

	public String saveRezidedImage(BufferedImage bufferedImage, int scale) throws IOException {
		try{
			return mediaDao.saveMedia(bufferedImage,scale);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	public String saveImage(BufferedImage bufferedImage) throws IOException {
		try{
			return mediaDao.saveMedia(bufferedImage);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
