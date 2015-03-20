package com.abhinav;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class StartOptions2 {

	private static final int DELAY = 20;
	public static int x_scale;
	public static int y_scale;
	private static boolean flag;
	static IplImage currentFrame = null;
	static IplImage prevFrame = null;
	static IplImage diffFrame = null;
	private static int posX;
	private static int posY;
	private static int pageNum = 0;
	private static int waitDelay;

	public static IplImage getOverlaidFrame(IplImage grabbedFrame)
			throws IOException {
		// TODO Auto-generated method stub

		BufferedImage grabbedImage = grabbedFrame.getBufferedImage();
		x_scale = grabbedFrame.width();
		y_scale = grabbedFrame.height();

		// Image overlay
		BufferedImage overlayM = ImageIO.read(new File("images/M.png"));
		BufferedImage overlayF = ImageIO.read(new File("images/F.png"));
		BufferedImage overlayK = ImageIO.read(new File("images/K.png"));
		BufferedImage overlayMtag = ImageIO.read(new File("images/Mtag.png"));
		BufferedImage overlayFtag = ImageIO.read(new File("images/Ftag.png"));
		BufferedImage overlayKtag = ImageIO.read(new File("images/Ktag.png"));
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

		if (pageNum == 0) {

			// Categories
			g2.setPaint(new Color(128, 255, 64, 200));
			g2.fillRect((int) (0.02 * x_scale), (int) (0.05 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));
			g2.fillRect((int) (0.02 * x_scale), (int) (0.3 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));
			g2.fillRect((int) (0.02 * x_scale), (int) (0.55 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));

			g2.setPaint(new Color(0, 64, 255, 200));
			g2.drawRect((int) (0.02 * x_scale), (int) (0.05 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));
			g2.drawRect((int) (0.02 * x_scale), (int) (0.3 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));
			g2.drawRect((int) (0.02 * x_scale), (int) (0.55 * y_scale),
					(int) (0.15 * x_scale), (int) (0.2 * y_scale));

			// image overlays
			g2D.drawImage(overlayM, (int) (0.045 * x_scale),
					(int) (0.06 * y_scale), (int) (0.1 * x_scale),
					(int) (0.18 * y_scale), null);
			g2D.drawImage(overlayF, (int) (0.045 * x_scale),
					(int) (0.31 * y_scale), (int) (0.1 * x_scale),
					(int) (0.18 * y_scale), null);
			g2D.drawImage(overlayK, (int) (0.045 * x_scale),
					(int) (0.56 * y_scale), (int) (0.1 * x_scale),
					(int) (0.18 * y_scale), null);

			if (waitDelay >= DELAY) {

				// text
				g2.setPaint(new Color(255, 255, 192, 192));
				g2.fillRect((int) (0.11 * x_scale), (int) (0.8 * y_scale),
						(int) (0.775 * x_scale), (int) (0.085 * x_scale));

				g2.setPaint(new Color(0, 0, 0, 192));
				g2.setFont(new Font("Sansserif", Font.BOLD,
						(int) (0.07 * x_scale)));
				g2.drawString("[", (int) (0.12 * x_scale),
						(int) (0.88 * y_scale));
				g2.setFont(new Font("Sansserif", Font.BOLD,
						(int) (0.05 * x_scale)));
				g2.drawString("Select Category on the Left.",
						(int) (0.17 * x_scale), (int) (0.88 * y_scale));
				g2.setFont(new Font("Sansserif", Font.BOLD,
						(int) (0.07 * x_scale)));
				g2.drawString("]", (int) (0.85 * x_scale),
						(int) (0.88 * y_scale));

				// Selected Category
				g2.setPaint(new Color(255, 64, 128, 255));

				if (VTR.CategoryNumber == 1) {
					g2.drawRect((int) (0.01 * x_scale), (int) (0.04 * y_scale),
							(int) (0.17 * x_scale), (int) (0.23 * y_scale));
					g2.setPaint(new Color(255, 255, 64, 200));
					g2.fillRect((int) (0.02 * x_scale), (int) (0.05 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2.setPaint(new Color(0, 64, 255, 200));
					g2.drawRect((int) (0.02 * x_scale), (int) (0.05 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2D.drawImage(overlayM, (int) (0.045 * x_scale),
							(int) (0.06 * y_scale), (int) (0.1 * x_scale),
							(int) (0.18 * y_scale), null);

					g2D.drawImage(overlayMtag, (int) (0.65 * x_scale),
							(int) (0.01 * y_scale), (int) (0.34 * x_scale),
							(int) (0.35 * y_scale), null);

					// Go Box
					// g2.fillRect((int) (0.8 * x_scale), (int) (0.43 *
					// y_scale),
					// (int) (0.18 * x_scale), (int) (0.16 * y_scale));
					g2.setPaint(new Color(255, 64, 128, 255));
					g2.fillRect((int) (0.8 * x_scale), (int) (0.43 * y_scale),
							(int) (0.18 * x_scale), (int) (0.16 * y_scale));

					g2.setPaint(new Color(128, 255, 64, 192));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.42 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.58 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.79 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));
					g2.fillRect((int) (0.97 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));

					g2.setPaint(new Color(0, 0, 0, 255));

					if (flag == true) {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.07 * x_scale)));
						g2.drawString("Go.", (int) (0.84 * x_scale),
								(int) (0.54 * y_scale));
						flag = false;
					} else {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.1 * x_scale)));
						g2.drawString("Go.", (int) (0.81 * x_scale),
								(int) (0.55 * y_scale));
						flag = true;
					}

				} else if (VTR.CategoryNumber == 2) {
					g2.drawRect((int) (0.01 * x_scale), (int) (0.29 * y_scale),
							(int) (0.17 * x_scale), (int) (0.23 * y_scale));
					g2.setPaint(new Color(255, 255, 64, 200));
					g2.fillRect((int) (0.02 * x_scale), (int) (0.3 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2.setPaint(new Color(0, 64, 255, 200));
					g2.drawRect((int) (0.02 * x_scale), (int) (0.3 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2D.drawImage(overlayF, (int) (0.045 * x_scale),
							(int) (0.31 * y_scale), (int) (0.1 * x_scale),
							(int) (0.18 * y_scale), null);
					
					g2D.drawImage(overlayFtag, (int) (0.65 * x_scale),
							(int) (0.01 * y_scale), (int) (0.34 * x_scale),
							(int) (0.35 * y_scale), null);
					
					// Go Box
					// g2.fillRect((int) (0.8 * x_scale), (int) (0.43 *
					// y_scale),
					// (int) (0.18 * x_scale), (int) (0.16 * y_scale));
					g2.setPaint(new Color(255, 64, 128, 255));
					g2.fillRect((int) (0.8 * x_scale), (int) (0.43 * y_scale),
							(int) (0.18 * x_scale), (int) (0.16 * y_scale));

					g2.setPaint(new Color(128, 255, 64, 192));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.42 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.58 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.79 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));
					g2.fillRect((int) (0.97 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));

					g2.setPaint(new Color(0, 0, 0, 255));

					if (flag == true) {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.07 * x_scale)));
						g2.drawString("Go.", (int) (0.84 * x_scale),
								(int) (0.54 * y_scale));
						flag = false;
					} else {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.1 * x_scale)));
						g2.drawString("Go.", (int) (0.81 * x_scale),
								(int) (0.55 * y_scale));
						flag = true;
					}

				} else if (VTR.CategoryNumber == 3) {
					g2.drawRect((int) (0.01 * x_scale), (int) (0.54 * y_scale),
							(int) (0.17 * x_scale), (int) (0.23 * y_scale));
					g2.setPaint(new Color(255, 255, 64, 200));
					g2.fillRect((int) (0.02 * x_scale), (int) (0.55 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2.setPaint(new Color(0, 64, 255, 200));
					g2.drawRect((int) (0.02 * x_scale), (int) (0.55 * y_scale),
							(int) (0.15 * x_scale), (int) (0.2 * y_scale));
					g2D.drawImage(overlayK, (int) (0.045 * x_scale),
							(int) (0.56 * y_scale), (int) (0.1 * x_scale),
							(int) (0.18 * y_scale), null);
					
					g2D.drawImage(overlayKtag, (int) (0.65 * x_scale),
							(int) (0.01 * y_scale), (int) (0.34 * x_scale),
							(int) (0.35 * y_scale), null);
					
					// Go Box
					// g2.fillRect((int) (0.8 * x_scale), (int) (0.43 *
					// y_scale),
					// (int) (0.18 * x_scale), (int) (0.16 * y_scale));
					g2.setPaint(new Color(255, 64, 128, 255));
					g2.fillRect((int) (0.8 * x_scale), (int) (0.43 * y_scale),
							(int) (0.18 * x_scale), (int) (0.16 * y_scale));

					g2.setPaint(new Color(128, 255, 64, 192));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.42 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.78 * x_scale), (int) (0.58 * y_scale),
							(int) (0.215 * x_scale), (int) (0.015 * x_scale));
					g2.fillRect((int) (0.79 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));
					g2.fillRect((int) (0.97 * x_scale), (int) (0.4 * y_scale),
							(int) (0.015 * x_scale), (int) (0.21 * y_scale));

					g2.setPaint(new Color(0, 0, 0, 255));

					if (flag == true) {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.07 * x_scale)));
						g2.drawString("Go.", (int) (0.84 * x_scale),
								(int) (0.54 * y_scale));
						flag = false;
					} else {
						g2.setFont(new Font("Sansserif", Font.BOLD,
								(int) (0.1 * x_scale)));
						g2.drawString("Go.", (int) (0.81 * x_scale),
								(int) (0.55 * y_scale));
						flag = true;
					}

				}

			}

		}

		if (waitDelay >= DELAY) {
			// Motion detection
			cvSmooth(grabbedFrame, grabbedFrame, CV_GAUSSIAN, 9, 9, 2, 2);
			if (currentFrame == null) {
				currentFrame = IplImage.create(grabbedFrame.width(),
						grabbedFrame.height(), IPL_DEPTH_8U, 1);
				cvCvtColor(grabbedFrame, currentFrame, CV_RGB2GRAY);
			} else {
				prevFrame = IplImage.create(grabbedFrame.width(),
						grabbedFrame.height(), IPL_DEPTH_8U, 1);
				prevFrame = currentFrame;
				currentFrame = IplImage.create(grabbedFrame.width(),
						grabbedFrame.height(), IPL_DEPTH_8U, 1);
				cvCvtColor(grabbedFrame, currentFrame, CV_RGB2GRAY);
			}
			if (diffFrame == null) {
				diffFrame = IplImage.create(grabbedFrame.width(),
						grabbedFrame.height(), IPL_DEPTH_8U, 1);
			}
			if (prevFrame != null) {
				// perform ABS difference
				cvAbsDiff(currentFrame, prevFrame, diffFrame);
				// do some threshold for wipe away useless details
				cvThreshold(diffFrame, diffFrame, 32, 255, CV_THRESH_BINARY);
				// recognize contours
				CvSeq contour = new CvSeq(null);
				cvFindContours(diffFrame, Overlay.storage, contour,
						Loader.sizeof(CvContour.class), CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE);
				while (contour != null && !contour.isNull()) {
					if (contour.elem_size() > 0) {
						CvBox2D box = cvMinAreaRect2(contour, Overlay.storage);
						// test intersection
						if (box != null) {
							CvPoint2D32f center = box.center();
							posX = (int) center.x();
							posY = (int) center.y();

							// Male
							/*
							 * g2.fillRect((int) (0.02 * x_scale), (int) (0.05 *
							 * y_scale), (int) (0.15 * x_scale), (int) (0.2 *
							 * y_scale));
							 */
							if (posX >= (int) (0.02 * x_scale)
									&& posX <= (int) (0.17 * x_scale)
									&& posY >= (int) (0.05 * y_scale)
									&& posY <= (int) (0.25 * y_scale)) {

								VTR.CategoryNumber = 1;
								VTR.overlayImagePath = "overlays/M/0.png";
							}

							// Female
							/*
							 * g2.fillRect((int) (0.02 * x_scale), (int) (0.3 *
							 * y_scale), (int) (0.15 * x_scale), (int) (0.2 *
							 * y_scale));
							 */
							if (posX >= (int) (0.02 * x_scale)
									&& posX <= (int) (0.17 * x_scale)
									&& posY >= (int) (0.3 * y_scale)
									&& posY <= (int) (0.5 * y_scale)) {

								VTR.CategoryNumber = 2;
								VTR.overlayImagePath = "overlays/F/0.png";
							}

							// Kids
							/*
							 * g2.fillRect((int) (0.02 * x_scale), (int) (0.55 *
							 * y_scale), (int) (0.15 * x_scale), (int) (0.2 *
							 * y_scale));
							 */
							if (posX >= (int) (0.02 * x_scale)
									&& posX <= (int) (0.17 * x_scale)
									&& posY >= (int) (0.55 * y_scale)
									&& posY <= (int) (0.75 * y_scale)) {

								VTR.CategoryNumber = 3;
								VTR.overlayImagePath = "overlays/K/0.png";
							}

							// Go Box
							/*
							 * g2.fillRect((int) (0.8 * x_scale), (int) (0.43 *
							 * y_scale), (int) (0.18 * x_scale), (int) (0.16 *
							 * y_scale));
							 */
							if (posX >= (int) (0.8 * x_scale)
									&& posX <= (int) (0.98 * x_scale)
									&& posY >= (int) (0.43 * y_scale)
									&& posY <= (int) (0.59 * y_scale)
									&& VTR.CategoryNumber != 0) {

								pageNum = 1;
								VTR.startOptions = false;

							}

						}
					}
					contour = contour.h_next();
				}
			}
		}

		waitDelay++;
		// cvClearMemStorage(storage);
		// grayFrame.release();
		g2.dispose();
		g2D.dispose();
		grabbedFrame = IplImage.createFrom(overlaidImage);
		g.dispose();

		return grabbedFrame;
	}
}
