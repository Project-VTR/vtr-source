package com.abhinav;

import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OptionsPanel {
	static JPanel panel = new JPanel();
	static int snapCount = 0;
	static int imageNumber = 0;

	public static JPanel getPanel() {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.blue);
		final JButton snap = new JButton("Snapshot");
		final JButton nextOverlay = new JButton("Try Next");
		final JButton zoomIn = new JButton("+");
		final JButton zoomOut = new JButton("-");
		final JButton up = new JButton("Up");
		final JButton down = new JButton("Down");
		panel.add(snap);
		panel.add(nextOverlay);
		panel.add(zoomIn);
		panel.add(zoomOut);
		panel.add(up);
		panel.add(down);

		snap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				takeSnapshot();
			}
		});

		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				zoomIn();
			}
		});

		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				zoomOut();
			}
		});

		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Overlay.horizontalDilsplacement -= 5;
			}
		});

		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Overlay.horizontalDilsplacement += 5;
			}
		});

		nextOverlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Overlay.SCALE = 0.25;
				imageNumber = (imageNumber + 1) % 23;
				VTR.overlayImagePath = "overlays/" + imageNumber + ".png";
			}

		});

		return panel;
	}

	protected static void takeSnapshot() {
		// TODO Auto-generated method stub
		try {
			final BufferedImage img = Overlay.snap.getBufferedImage();
			final JFrame f = new JFrame("Snapshot");
			JMenuBar menubar = new JMenuBar();
			JButton buttonSave = new JButton("Save");
			JButton buttonRetake = new JButton("Retake");
			menubar.add(buttonSave);
			menubar.add(buttonRetake);
			f.setJMenuBar(menubar);

			buttonSave.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					snapCount++;
					String imgName = "Snap-" + snapCount + ".jpg";
					/*
					 * try { Screenshot.writeImage(img, imgName); } catch
					 * (IOException e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 */
					cvSaveImage(imgName, IplImage.createFrom(img));
					Overlay.snapshotEnabled = true;
				}
			});

			buttonRetake.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					f.dispose();
					Overlay.snapshotEnabled = true;
				}
			});

			f.setContentPane(new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(img.getWidth(), img.getHeight());
				}
			});
			f.pack();
			f.setVisible(true);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setLocation(100, 100);
			f.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					// TODO Auto-generated method stub
					Overlay.snapshotEnabled = true;
				}

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static void zoomOut() {
		if (Overlay.SCALE > 0.15) {
			// TODO Auto-generated method stub
			Overlay.SCALE -= .0125;
		}
	}

	protected static void zoomIn() {
		if (Overlay.SCALE < 0.50) {
			// TODO Auto-generated method stub
			Overlay.SCALE += .0125;
		}
	}
}
