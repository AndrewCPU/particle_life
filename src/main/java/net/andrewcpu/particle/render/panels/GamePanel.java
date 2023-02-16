package net.andrewcpu.particle.render.panels;

import net.andrewcpu.particle.util.ResourceLoader;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JFrame {
	public static WorldCanvas worldCanvas;
	@Override
	public void dispose() {
		worldCanvas.dispose();
		super.dispose();
		System.exit(0);
	}

	public static Font mainFont;
	public static Font thinFont;

	private void loadFont(){
		mainFont = ResourceLoader.getInstance().loadFont("/fonts/Roboto-Bold.ttf");
		thinFont = ResourceLoader.getInstance().loadFont("/fonts/Roboto-Regular.ttf");
	}

	public GamePanel() {
		try {
			loadFont();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		worldCanvas = new WorldCanvas(Integer.parseInt(JOptionPane.showInputDialog("How many particles?")));
		OptionPane optionPane = new OptionPane();
		setSize(1500,900);
		getContentPane().addKeyListener(worldCanvas);
		getContentPane().addMouseListener(worldCanvas);
		getContentPane().addMouseWheelListener(worldCanvas);
		getContentPane().addMouseMotionListener(worldCanvas);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		worldCanvas.setPreferredSize(new Dimension(getWidth() * 7 / 10, getHeight()));
		optionPane.setPreferredSize(new Dimension(getWidth() * 3 / 10, getHeight()));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(worldCanvas, BorderLayout.WEST);
		getContentPane().add(optionPane, BorderLayout.EAST);
		setVisible(true);
		worldCanvas.setFocusable(true);
		worldCanvas.requestFocusInWindow();
		setFocusable(true);
		getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				worldCanvas.setPreferredSize(new Dimension((int)(e.getComponent().getWidth() - optionPane.getSize().getWidth()), e.getComponent().getHeight()));
				worldCanvas.setSize(new Dimension((int)(e.getComponent().getWidth() - optionPane.getSize().getWidth()), e.getComponent().getHeight()));
				optionPane.setSize(new Dimension(e.getComponent().getWidth() * 3 / 10, e.getComponent().getHeight()));
				revalidate();
			}

			// ...
		});
		worldCanvas.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				worldCanvas.requestFocus();
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {

			}

			@Override
			public void ancestorMoved(AncestorEvent event) {

			}
		});
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			worldCanvas.repaint();
		}, 20, 20, TimeUnit.MILLISECONDS);
	}
}
