package com.roulette.resto.common.dao;

import com.roulette.resto.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Repository
@Slf4j
public class MediaDao {

	@Value("${app.storage.images.location}")
	private String imageDir;


	public String saveMedia(BufferedImage bufferedImage) throws IOException {
		try{
			String uuid = UUID.randomUUID().toString();
			ImageIO.write(bufferedImage, "jpg", new File(imageDir+"/"+uuid+".jpg"));
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
