package com.abhinav;

import static com.googlecode.javacv.cpp.opencv_core.cvFlip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect;

public class VTR {
	private static final int DELAY = 100;
	static CanvasFrame f;
	static FrameGrabber grabber;
	static IplImage grabbedFrame;
	private IplImage superImposedFrame;
	private int optionsDelay = 0;
	private boolean flag = false;
	static int startOptionNumber = 1;
	public static JButton snap;
	static String overlayImagePath = "overlays/0.png";
	static Boolean optionsEnabled = false;
	static Boolean startOptions = true;
	static int CategoryNumber = 0;

	public VTR() {
		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		// Setting the main frame
		f = new CanvasFrame("Virtual Trial Room", 1.0);
		f.setIconImage(new ImageIcon("images/icon.png").getImage());
		f.setBackground(Color.black);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);

		// Setting the Snapshot Button
		snap = new JButton(" Take Snapshot");
		snap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				OptionsPanel.takeSnapshot();
			}
		});
		f.getContentPane().add(BorderLayout.SOUTH, snap);
		f.getContentPane().getComponent(1).setVisible(false);
		f.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && flag == true)
					OptionsPanel.takeSnapshot();
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && flag == true)
					OptionsPanel.takeSnapshot();
			}
		});
		f.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if (flag == true)
					OptionsPanel.takeSnapshot();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if (flag == true)
					OptionsPanel.takeSnapshot();
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if (flag == true)
					OptionsPanel.takeSnapshot();
			}
		});

		// Setting the Webcam media on the Canvasframe
		grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			grabbedFrame = grabber.grab();
			while (f.isVisible() && (grabbedFrame = grabber.grab()) != null) {
				// lateral inversion of grabbed image
				cvFlip(grabbedFrame, grabbedFrame, 1);

				if (startOptions == true) {
					if (startOptionNumber == 1) {
						superImposedFrame = StartOptions
								.getOverlaidFrame(grabbedFrame);
					} else if (startOptionNumber == 2) {
						superImposedFrame = StartOptions2
								.getOverlaidFrame(grabbedFrame);
					}
				} else {
					if (optionsDelay >= DELAY) {
						optionsEnabled = true;
						flag = true;
					} else {
						optionsDelay++;
						superImposedFrame = StartOptions3
								.getOverlaidFrame(grabbedFrame);
					}
					if (flag == true) {
						superImposedFrame = Overlay.getOverlaidFrame(
								grabbedFrame, overlayImagePath, optionsEnabled);
						f.getContentPane().getComponent(1).setVisible(true);
					}
				}
				f.showImage(superImposedFrame);
			}
			// grabbedFrame.release();
			grabber.stop();
		} catch (IOException e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(f, e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(f, e.toString());
		}
		f.dispose();
	}

	public static void main(final String[] args) {
		new VTR();
		System.exit(0);
	}
}
