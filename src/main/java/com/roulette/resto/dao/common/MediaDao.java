package com.roulette.resto.dao.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Repository
@Slf4j
public class MediaDao {

	@Value("${app.storage.images.location}")
	private String imageDir;


	public String saveMedia(BufferedImage bufferedImage, int maxTotalPixels) throws IOException {
		int originalWidth = bufferedImage.getWidth();
		int originalHeight = bufferedImage.getHeight();
		long originalTotalPixels = (long) originalWidth * originalHeight;

		if (originalTotalPixels >= maxTotalPixels) {
			bufferedImage = resize(bufferedImage, maxTotalPixels);
		}
		try {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			String outputImgPath = imageDir + "/" + uuid + ".jpeg";
			FileOutputStream outputStream = new FileOutputStream(outputImgPath);
			BufferedImage withoutAlpha = new BufferedImage( bufferedImage.getWidth(),bufferedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics g = withoutAlpha.getGraphics();
			g.drawImage(bufferedImage, 0, 0, null);
			g.dispose();

			boolean result = ImageIO.write(
					withoutAlpha, "JPEG", outputStream);
			if (!result) {
				throw new IOException("Could not save image");
			}
			outputStream.close();
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public String saveSquaredMedia(BufferedImage originalImage, int maxResolution) throws IOException {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		int squareSize = Math.min(width, height);
		int x = (width - squareSize) / 2;
		int y = (height - squareSize) / 2;
		BufferedImage squareImage = originalImage.getSubimage(x, y, squareSize, squareSize);
		return saveMedia(squareImage, maxResolution);
	}
	private BufferedImage resize(BufferedImage originalImage, int maxTotalPixels)  {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		long originalTotalPixels = (long) originalWidth * originalHeight;

		double scaleFactor = Math.sqrt((double) maxTotalPixels / originalTotalPixels);
		int newWidth = (int) Math.round(originalWidth * scaleFactor);
		int newHeight = (int) Math.round(originalHeight * scaleFactor);

		int imageType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);

		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, newWidth, newHeight, null);
		g.dispose();

		return resizedImage;
	}

	public boolean removeMedia(String uuid) throws IOException {
		File fileToDelete = FileUtils.getFile(imageDir + "/" + uuid + ".jpeg");
		boolean success = FileUtils.deleteQuietly(fileToDelete);
		if (!success) {
			throw new IOException("Failed to delete file");
		}
		return success;
	}
}
