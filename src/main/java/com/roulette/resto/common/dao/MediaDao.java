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

	@Value("app.storage.images.maxResolution")
	private int maxResolution;


	public String saveMedia(BufferedImage bufferedImage, int scale) throws IOException {
		try{
			final int w = bufferedImage.getWidth();
			final int h = bufferedImage.getHeight();
			BufferedImage scaledImage = new BufferedImage((w * scale), (h * scale), BufferedImage.TYPE_INT_ARGB);
			String uuid = UUID.randomUUID().toString();
			boolean saved = ImageIO.write(scaledImage, "png", new File(imageDir+"/"+uuid+".png"));
			if(!saved) {
				throw new IOException("Could not save image");
			}
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	public String saveMedia(BufferedImage bufferedImage) throws IOException {

		if(bufferedImage.getHeight() * bufferedImage.getWidth() >maxResolution){
			return this.saveMedia(new BufferedImage((int) (bufferedImage.getWidth() * 0.25),
					(int) (bufferedImage.getHeight() * 0.25),
					BufferedImage.TYPE_INT_ARGB));
		}
		return this.saveMedia(bufferedImage);
	}
}
