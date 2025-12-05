package com.roulette.resto.common.dao;

import com.roulette.resto.common.entity.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Repository
@Slf4j
public class MediaDao {

	@Value("${app.storage.images.location}")
	private String imageDir;


	private String saveMedia(BufferedImage bufferedImage) throws IOException {
		try {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			boolean saved = ImageIO.write(bufferedImage, "png", new File(imageDir + "/" + uuid + ".png"));
			if (!saved) {
				throw new IOException("Could not save image");
			}
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	public String saveMedia(BufferedImage img, int maxResolution ) throws IOException {
		try {
			log.info("Saving media");
			long resolution = (long) img.getWidth() * img.getHeight();
			if (resolution > maxResolution) {
				int newW = (int) (img.getWidth() * 0.25);
				int newH = (int) (img.getHeight() * 0.25);

				BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = resized.createGraphics();

				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(img, 0, 0, newW, newH, null);
				g.dispose();

				return saveMedia(resized, maxResolution);
			}
			return saveMedia(img);
		}catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}

	}

}
