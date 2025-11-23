package com.roulette.resto.common.service;

import com.roulette.resto.common.dao.MediaDao;
import com.roulette.resto.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Service
@Slf4j
public class MediaService {


	private MediaDao mediaDao;
	
	public String saveImage(BufferedImage bufferedImage){
		try{
			return mediaDao.saveMedia(bufferedImage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
