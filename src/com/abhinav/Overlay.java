package com.abhinav;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
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
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

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
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize2D32f;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class Overlay {
	static double SCALE = 0.25;
	static int horizontalDilsplacement = 50;
	private final static String FACE_CASCADE_FILE = "cascades\\haarcascade_frontalface_alt.xml";
	static CvRect r = null;
	private static int face_x;
	private static int face_y;
	private static int face_h;
	private static int face_w;
	private static int x = 90;
	private static int y = 20;
	private static int h;
	private static int w;
	static Boolean snapshotEnabled = true;
	private static CvHaarClassifierCascade cascade_face;
	static CvMemStorage storage;
	private static int x_scale;
	private static int y_scale;
	static IplImage snap;
	private static int posX;
	private static int posY;
	static IplImage currentFrame = null;
	static IplImage prevFrame = null;
	static IplImage diffFrame = null;
	static int up = 0;
	static int down = 0;
	static int zoomIn = 0;
	static int zoomOut = 0;
	static int previous = 0;
	static int next = 0;
	private static int DELAY_NEXT = 25;
	private static int DELAY_OPTIONS = 4;
	static int OVERLAY_COUNT_M = 14;
	static int OVERLAY_COUNT_F = 13;
	static int OVERLAY_COUNT_K = 17;
	private static boolean faceMoved;
	static {
		cascade_face = new CvHaarClassifierCascade(cvLoad(FACE_CASCADE_FILE));
		storage = CvMemStorage.create();
	}

	public static IplImage getOverlaidFrame(IplImage grabbedFrame,
			String overlayImagePath, Boolean optionsEnabled) throws IOException {

		BufferedImage grabbedImage = grabbedFrame.getBufferedImage();
		BufferedImage overlay = ImageIO.read(new File(overlayImagePath));
		w = (int) (overlay.getWidth() * SCALE);
		h = (int) (overlay.getHeight() * SCALE);

		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		// Face Detection
		IplImage grayFrame = IplImage.create(grabbedFrame.width(),
				grabbedFrame.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(grabbedFrame, grayFrame, CV_BGR2GRAY);
		CvSeq faces = cvHaarDetectObjects(grayFrame, cascade_face, storage,
				1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
		cvClearMemStorage(storage);

		if (faces.total() != 0) {
			r = new CvRect(cvGetSeqElem(faces, faces.total() - 1));
			face_x = r.x();
			face_y = r.y();
			face_w = r.width();
			face_h = r.height();

			x = face_x + face_w / 2 - w / 2;
			y = face_y + face_h / 2 + horizontalDilsplacement;

		}

		// Image overlay
		BufferedImage overlaidImage = new BufferedImage(
				grabbedImage.getWidth(), grabbedImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = overlaidImage.createGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.drawImage(grabbedImage, 0, 0, grabbedImage.getWidth(),
				grabbedImage.getHeight(), null);
		y = face_y + face_h / 2 + horizontalDilsplacement;
		g2D.drawImage(overlay, x, y, w, h, null);
		snap = IplImage.createFrom(overlaidImage);

		// Transparent overlay
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		if (optionsEnabled == true) {
			x_scale = grabbedFrame.width();
			y_scale = grabbedFrame.height();
			g2.setPaint(new Color(0, 192, 0, 192));

			// zoom in
			g2.drawOval((int) (0.11 * x_scale), 0, (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));
			// g2.drawRect((int) (0.12 * x_scale), (int) (0.02 * y_scale),
			// (int) (0.14 * x_scale), (int) (0.18 * y_scale));
			// zoom out
			g2.drawOval(0, (int) (0.25 * y_scale), (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));
			// g2.drawRect((int) (0.01 * x_scale), (int) (0.27 * y_scale),
			// (int) (0.14 * x_scale), (int) (0.18 * y_scale));

			// up
			g2.drawOval((int) (0.73 * x_scale), 0, (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));
			// g2.drawRect((int) (0.74 * x_scale), (int) (0.02 * y_scale),
			// (int) (0.14 * x_scale), (int) (0.18 * y_scale));
			// down
			g2.drawOval((int) (0.84 * x_scale), (int) (0.25 * y_scale),
					(int) (0.16 * x_scale), (int) (0.16 * x_scale));
			// g2.drawRect((int) (0.85 * x_scale), (int) (0.27 * y_scale),
			// (int) (0.14 * x_scale), (int) (0.18 * y_scale));

			// previous
			g2.drawRect((int) (0.01 * x_scale), (int) (0.67 * y_scale),
					(int) (0.18 * x_scale), (int) (0.12 * y_scale));
			// next
			g2.drawRect((int) (0.81 * x_scale), (int) (0.67 * y_scale),
					(int) (0.18 * x_scale), (int) (0.12 * y_scale));

			g2.setPaint(new Color(255, 255, 0, 64));

			// zoom in
			g2.fillOval((int) (0.11 * x_scale), 0, (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));
			// zoom out
			g2.fillOval(0, (int) (0.25 * y_scale), (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));

			// up
			g2.fillOval((int) (0.73 * x_scale), 0, (int) (0.16 * x_scale),
					(int) (0.16 * x_scale));
			// down
			g2.fillOval((int) (0.84 * x_scale), (int) (0.25 * y_scale),
					(int) (0.16 * x_scale), (int) (0.16 * x_scale));

			// previous
			g2.fillRect((int) (0.01 * x_scale), (int) (0.67 * y_scale),
					(int) (0.18 * x_scale), (int) (0.12 * y_scale));
			// next
			g2.fillRect((int) (0.81 * x_scale), (int) (0.67 * y_scale),
					(int) (0.18 * x_scale), (int) (0.12 * y_scale));

			g2.setPaint(new Color(0, 64, 255, 128));
			g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.15 * x_scale)));
			g2.drawString("+", (int) (0.1475 * x_scale), (int) (0.18 * y_scale));
			g2.drawString("-", (int) (0.055 * x_scale), (int) (0.41 * y_scale));

			g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.12 * x_scale)));
			g2.drawString("↑", (int) (0.7725 * x_scale),
					(int) (0.165 * y_scale));
			g2.drawString("↓", (int) (0.885 * x_scale), (int) (0.42 * y_scale));

			g2.setPaint(new Color(0, 0, 0, 150));
			g2.setFont(new Font("Sansserif", Font.BOLD, (int) (0.05 * x_scale)));
			g2.drawString("Prev.", (int) (0.04 * x_scale),
					(int) (0.75 * y_scale));
			g2.drawString("Next", (int) (0.845 * x_scale),
					(int) (0.75 * y_scale));

			// Motion detection
			faceMoved = false;
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
				cvFindContours(diffFrame, storage, contour,
						Loader.sizeof(CvContour.class), CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE);
				while (contour != null && !contour.isNull()) {
					if (contour.elem_size() > 0) {
						CvBox2D box = cvMinAreaRect2(contour, storage);
						// test intersection
						if (box != null) {
							CvPoint2D32f center = box.center();
							posX = (int) center.x();
							posY = (int) center.y();

							// zoomIn
							if (zoomIn == 0) {
								// g2.drawRect((int) (0.12 * x_scale), (int)
								// (0.02 * y_scale),
								// (int) (0.14 * x_scale), (int) (0.18 *
								// y_scale));
								if (posX >= (int) (0.12 * x_scale)
										&& posX <= (int) (0.26 * x_scale)
										&& posY >= (int) (0.02 * y_scale)
										&& posY <= (int) (0.2 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillOval((int) (0.11 * x_scale), 0,
											(int) (0.16 * x_scale),
											(int) (0.16 * x_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.15 * x_scale)));
									g2.drawString("+",
											(int) (0.1475 * x_scale),
											(int) (0.18 * y_scale));
									OptionsPanel.zoomIn();
									zoomIn = (zoomIn + 1) % DELAY_OPTIONS;
								}
							} else {
								zoomIn = (zoomIn + 1) % DELAY_OPTIONS;
							}

							// zoomOut
							if (zoomOut == 0) {
								// g2.drawRect((int) (0.01 * x_scale), (int)
								// (0.27 * y_scale),
								// (int) (0.14 * x_scale), (int) (0.18 *
								// y_scale));
								if (posX >= (int) (0.01 * x_scale)
										&& posX <= (int) (0.15 * x_scale)
										&& posY >= (int) (0.27 * y_scale)
										&& posY <= (int) (0.45 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillOval(0, (int) (0.25 * y_scale),
											(int) (0.16 * x_scale),
											(int) (0.16 * x_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.15 * x_scale)));
									g2.drawString("-", (int) (0.055 * x_scale),
											(int) (0.41 * y_scale));
									OptionsPanel.zoomOut();
									zoomOut = (zoomOut + 1) % DELAY_OPTIONS;
								}
							} else {
								zoomOut = (zoomOut + 1) % DELAY_OPTIONS;
							}

							// up
							if (up == 0) {
								// g2.drawRect((int) (0.74 * x_scale), (int)
								// (0.02 * y_scale),
								// (int) (0.14 * x_scale), (int) (0.18 *
								// y_scale));
								if (posX >= (int) (0.74 * x_scale)
										&& posX <= (int) (0.88 * x_scale)
										&& posY >= (int) (0.02 * y_scale)
										&& posY <= (int) (0.2 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillOval((int) (0.73 * x_scale), 0,
											(int) (0.16 * x_scale),
											(int) (0.16 * x_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.12 * x_scale)));
									g2.drawString("↑",
											(int) (0.7725 * x_scale),
											(int) (0.165 * y_scale));
									if (Overlay.horizontalDilsplacement >= 25) {
										Overlay.horizontalDilsplacement -= 5;
									}
									up = (up + 1) % DELAY_OPTIONS;
								}
							} else {
								up = (up + 1) % DELAY_OPTIONS;
							}

							// down
							if (down == 0) {
								// g2.drawRect((int) (0.85 * x_scale), (int)
								// (0.27 * y_scale),
								// (int) (0.14 * x_scale), (int) (0.18 *
								// y_scale));
								if (posX >= (int) (0.85 * x_scale)
										&& posX <= (int) (0.99 * x_scale)
										&& posY >= (int) (0.27 * y_scale)
										&& posY <= (int) (0.45 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillOval((int) (0.84 * x_scale),
											(int) (0.25 * y_scale),
											(int) (0.16 * x_scale),
											(int) (0.16 * x_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.12 * x_scale)));
									g2.drawString("↓", (int) (0.885 * x_scale),
											(int) (0.42 * y_scale));
									if (Overlay.horizontalDilsplacement <= 100) {
										Overlay.horizontalDilsplacement += 5;
									}
									down = (down + 1) % DELAY_OPTIONS;
								}
							} else {
								down = (down + 1) % DELAY_OPTIONS;
							}

							// previous
							if (previous == 0) {
								// g2.fillRect((int) (0.01 * x_scale), (int)
								// (0.67 *
								// y_scale),
								// (int)
								// (0.18 * x_scale), (int) (0.12 * y_scale));
								if (posX >= (int) (0.01 * x_scale)
										&& posX <= (int) (0.19 * x_scale)
										&& posY >= (int) (0.67 * y_scale)
										&& posY <= (int) (0.79 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillRect((int) (0.01 * x_scale),
											(int) (0.67 * y_scale),
											(int) (0.18 * x_scale),
											(int) (0.12 * y_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.05 * x_scale)));
									g2.drawString("Prev.",
											(int) (0.04 * x_scale),
											(int) (0.75 * y_scale));
									if (VTR.CategoryNumber == 1) {
										if (OptionsPanel.imageNumber == 0) {
											OptionsPanel.imageNumber = OVERLAY_COUNT_M;
										}
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber - 1)
												% OVERLAY_COUNT_M;
										VTR.overlayImagePath = "overlays/M/"
												+ OptionsPanel.imageNumber
												+ ".png";
									} else if (VTR.CategoryNumber == 2) {
										if (OptionsPanel.imageNumber == 0) {
											OptionsPanel.imageNumber = OVERLAY_COUNT_F;
										}
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber - 1)
												% OVERLAY_COUNT_F;
										VTR.overlayImagePath = "overlays/F/"
												+ OptionsPanel.imageNumber
												+ ".png";
									} else if (VTR.CategoryNumber == 3) {
										if (OptionsPanel.imageNumber == 0) {
											OptionsPanel.imageNumber = OVERLAY_COUNT_K;
										}
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber - 1)
												% OVERLAY_COUNT_K;
										VTR.overlayImagePath = "overlays/K/"
												+ OptionsPanel.imageNumber
												+ ".png";
									}
									previous = (previous + 1) % DELAY_NEXT;
								}
							} else {
								previous = (previous + 1) % DELAY_NEXT;
							}

							// next
							if (next == 0) {
								// g2.fillRect((int) (0.81 * x_scale), (int)
								// (0.67 *
								// y_scale),(int)
								// (0.18 * x_scale), (int) (0.12 * y_scale));
								if (posX >= (int) (0.81 * x_scale)
										&& posX <= (int) (0.99 * x_scale)
										&& posY >= (int) (0.67 * y_scale)
										&& posY <= (int) (0.79 * y_scale)) {
									g2.setPaint(new Color(255, 64, 128, 255));
									g2.fillRect((int) (0.81 * x_scale),
											(int) (0.67 * y_scale),
											(int) (0.18 * x_scale),
											(int) (0.12 * y_scale));
									g2.setPaint(new Color(0, 64, 255, 128));
									g2.setFont(new Font("Sansserif", Font.BOLD,
											(int) (0.05 * x_scale)));
									g2.drawString("Next",
											(int) (0.845 * x_scale),
											(int) (0.75 * y_scale));
									if (VTR.CategoryNumber == 1) {
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber + 1)
												% OVERLAY_COUNT_M;
										VTR.overlayImagePath = "overlays/M/"
												+ OptionsPanel.imageNumber
												+ ".png";
									} else if (VTR.CategoryNumber == 2) {
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber + 1)
												% OVERLAY_COUNT_F;
										VTR.overlayImagePath = "overlays/F/"
												+ OptionsPanel.imageNumber
												+ ".png";
									} else if (VTR.CategoryNumber == 3) {
										OptionsPanel.imageNumber = (OptionsPanel.imageNumber + 1)
												% OVERLAY_COUNT_K;
										VTR.overlayImagePath = "overlays/K/"
												+ OptionsPanel.imageNumber
												+ ".png";
									}
									next = (next + 1) % DELAY_NEXT;
								}
							} else {
								next = (next + 1) % DELAY_NEXT;
							}

							// face motion check
							if (posX >= (face_x - (face_w / 2))
									&& posX <= (face_x + face_w + (face_w / 2))
									&& posY >= (face_y - (face_h / 2))
									&& posY <= (face_y + face_h + (face_h / 2))) {
								faceMoved = true;
							}
						}
					}
					contour = contour.h_next();
				}

				// perform ABS difference
				cvAbsDiff(currentFrame, prevFrame, diffFrame);
				// do some threshold for wipe away useless details
				cvThreshold(diffFrame, diffFrame, 3, 255, CV_THRESH_BINARY);
				// recognize contours
				cvFindContours(diffFrame, storage, contour,
						Loader.sizeof(CvContour.class), CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE);
				while (contour != null && !contour.isNull()) {
					if (contour.elem_size() > 0) {
						CvBox2D box = cvMinAreaRect2(contour, storage);
						// test intersection
						if (box != null) {
							CvPoint2D32f center = box.center();
							posX = (int) center.x();
							posY = (int) center.y();
							CvSize2D32f boxSize = box.size();
							if (snapshotEnabled == true) {
								// smile motion detection
								if (posX >= (face_x + face_w / 8)
										&& posX <= (face_x + 7 * face_w / 8)
										&& posY >= (face_y + face_h / 2)
										&& posY <= (face_y + face_h)
										&& faceMoved == false
										&& boxSize.height() < 1.5
										&& boxSize.width() < 1.5) {
									snapshotEnabled = false;
									//OptionsPanel.takeSnapshot();
								} else
									break;
							}
						}
					}
					contour = contour.h_next();
				}

			}

		}

		// cvReleaseImage(grayFrame);
		cvClearMemStorage(storage);
		grayFrame.release();
		g2.dispose();
		g2D.dispose();
		grabbedFrame = IplImage.createFrom(overlaidImage);
		g.dispose();

		return grabbedFrame;
	}
}
