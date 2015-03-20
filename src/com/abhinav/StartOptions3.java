package com.abhinav;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class StartOptions3 {

	private static final int DELAY = 3;
	private static int x_scale;
	private static int y_scale;
	static IplImage currentFrame = null;
	static IplImage prevFrame = null;
	static IplImage diffFrame = null;
	private static boolean flag = true;
	private static int waitDelay = 0;

	public static IplImage getOverlaidFrame(IplImage grabbedFrame)
			throws IOException {
		// TODO Auto-generated method stub

		BufferedImage grabbedImage = grabbedFrame.getBufferedImage();
		x_scale = grabbedFrame.width();
		y_scale = grabbedFrame.height();

		BufferedImage overlaidImage = new BufferedImage(
				grabbedImage.getWidth(), grabbedImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = overlaidImage.createGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.drawImage(grabbedImage, 0, 0, grabbedImage.getWidth(),
				grabbedImage.getHeight(), null);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		// Position Hint
		if (waitDelay == DELAY) {
			if (flag == true)
				flag = false;
			else
				flag = true;
			waitDelay = 0;
		} else {
			waitDelay++;
		}
		g2.setPaint(new Color(128, 255, 64, 192));
		if (flag == true) {
			g2.fillRect((int) (0.3 * x_scale), (int) (0.03 * y_scale),
					(int) (0.1 * x_scale), (int) (0.03 * y_scale));
			g2.fillRect((int) (0.6 * x_scale), (int) (0.03 * y_scale),
					(int) (0.1 * x_scale), (int) (0.03 * y_scale));
			g2.fillRect((int) (0.3 * x_scale), (int) (0.03 * y_scale),
					(int) (0.03 * y_scale), (int) (0.4 * y_scale));
			g2.fillRect((int) (0.7 * x_scale) - (int) (0.03 * y_scale),
					(int) (0.03 * y_scale), (int) (0.03 * y_scale),
					(int) (0.4 * y_scale));
		} else {
			g2.fillRect((int) (0.32 * x_scale), (int) (0.06 * y_scale),
					(int) (0.1 * x_scale), (int) (0.03 * y_scale));
			g2.fillRect((int) (0.58 * x_scale), (int) (0.06 * y_scale),
					(int) (0.1 * x_scale), (int) (0.03 * y_scale));
			g2.fillRect((int) (0.32 * x_scale), (int) (0.06 * y_scale),
					(int) (0.03 * y_scale), (int) (0.4 * y_scale));
			g2.fillRect((int) (0.68 * x_scale) - (int) (0.03 * y_scale),
					(int) (0.06 * y_scale), (int) (0.03 * y_scale),
					(int) (0.4 * y_scale));
		}

		// text
		g2.setPaint(new Color(255, 255, 192, 192));
		g2.fillRect((int) (0.2 * x_scale), (int) (0.7 * y_scale),
				(int) (0.6 * x_scale), (int) (0.12 * y_scale));
		g2.setPaint(new Color(0, 0, 0, 192));
		g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.07 * x_scale)));
		g2.drawString("[", (int) (0.23 * x_scale), (int) (0.78 * y_scale));
		g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.05 * x_scale)));
		g2.drawString("Stand at the Centre.", (int) (0.27 * x_scale),
				(int) (0.78 * y_scale));
		g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.07 * x_scale)));
		g2.drawString("]", (int) (0.75 * x_scale), (int) (0.78 * y_scale));

		// cvClearMemStorage(storage);
		// grayFrame.release();
		g2.dispose();
		g2D.dispose();
		grabbedFrame = IplImage.createFrom(overlaidImage);
		g.dispose();

		return grabbedFrame;
	}
}
