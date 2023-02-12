package org.example;

import org.example.render.Renderer;
import org.example.render.Viewport;
import org.example.shader.HighSpeedDistanceCalculation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main extends JFrame {
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		int size = 750;
		setBounds(0, 0, size * 2, size);
		WorldCanvas worldCanvas = new WorldCanvas(10000);
		worldCanvas.setBounds(0, 0, size * 2, size);
		add(worldCanvas);
		addKeyListener(worldCanvas);
		addMouseListener(worldCanvas);
		addMouseWheelListener(worldCanvas);
		addMouseMotionListener(worldCanvas);
		setVisible(true);
		new OptionPanel();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			worldCanvas.repaint();
		}, 20, 20, TimeUnit.MILLISECONDS);
	}
}

class OptionPanel extends JFrame {
	public OptionPanel() {
		setBounds(0, 0, 500, 500);
		setLayout(new GridLayout(4, 1));
		JSlider drag = new JSlider();
		drag.setMinimum(0);
		drag.setMaximum(100);
		drag.setValue((int) (WorldConstant.DRAG * 100.0));
		drag.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				WorldConstant.DRAG = drag.getValue() / 100.0;
			}
		});

		JSlider distance = new JSlider();
		distance.setMinimum(0);
		distance.setMaximum(2000);
		distance.setValue((int) (WorldConstant.REPELLING_MULTIPLIER * 100.0));
		distance.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				WorldConstant.REPELLING_MULTIPLIER = distance.getValue() / 100.0;
			}
		});

		JSlider min = new JSlider();
		min.setMinimum(0);
		min.setMaximum(100);
		min.setValue((int) WorldConstant.MINIMUM_DISTANCE);

		min.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				WorldConstant.MINIMUM_DISTANCE = min.getValue();
			}
		});

		JSlider max = new JSlider();
		max.setMinimum(0);
		max.setMaximum(500);
		max.setValue((int) WorldConstant.MAXIMUM_DISTANCE);
		max.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				WorldConstant.MAXIMUM_DISTANCE = max.getValue();
			}
		});
		getContentPane().add(drag);
		getContentPane().add(distance);
		getContentPane().add(min);
		getContentPane().add(max);
		setVisible(true);
	}
}

class WorldCanvas extends JComponent implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	private World world;
	private HashMap<Integer, Double> threadTime = new HashMap<>();

	private void printTimes() {
		List<Integer> keys = threadTime.keySet().stream().sorted().collect(Collectors.toList());
		for (Integer i : keys) {
			System.out.println(String.format("%04d,%s,%s", i, new DecimalFormat("#.0000").format(threadTime.get(i).doubleValue()) + "", world.getSize() + ""));
		}
	}

	private int size = 5000;

	public WorldCanvas(int size) {
//		world = new World(6500);
		addKeyListener(this);
		world = new World(19, size);
		currentThreads++;
//		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//			double average = world.getAverageTime();
//			threadTime.put(currentThreads - 1,average);
//			printTimes();
//			disposeWorld();
//		}, 10, 10, TimeUnit.SECONDS);
//		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//			world.dispose();
//			world = new World(world.getSize());
//			offsetY = 0;
//			offsetX = 0;
//		}, 30, 30, TimeUnit.SECONDS);
	}

	private int offsetX = 0;
	private int offsetY = 0;
	private double scale = 1.0;

	private Renderer renderer = new Renderer();
	private List<Long> times = new ArrayList<>();

	@Override
	public void paint(Graphics g) {
//		g.fillRect(0, 0, getWidth(), getHeight());
		Viewport viewport = new Viewport(new Rectangle(new Point((int) offsetX, (int) offsetY), new Dimension((int) (getWidth()), (int) (getHeight()))), scale);
		long start = System.currentTimeMillis();
		BufferedImage rendered = renderer.render(viewport, world);
		g.drawImage(rendered, 0, 0, getWidth(), getHeight(), null);
		times.add(System.currentTimeMillis() - start);
		if (times.size() % 150 == 0) {
//			System.out.println("Average frame time: " + times.stream().mapToDouble(l -> l).average().getAsDouble() + "ms");
		}

	/*	for (Particle particle : world.getParticles()) {
			g.setColor(particle.getType().getColor());
			g.fillOval((int) particle.getPoint().getX() + offsetX, (int) particle.getPoint().getY() + offsetY, 10, 10);
		}*/

		int squareSize = 20;
		for (int x = 0; x < ParticleType.values().length; x++) {
			g.setColor(ParticleType.values()[x].getColor());
			g.fillRect(squareSize + (x * squareSize) + 10, 10, squareSize, squareSize);
			g.fillRect(10, squareSize + (x * squareSize) + 10, squareSize, squareSize);
			for (int y = 0; y < ParticleType.values().length; y++) {
				int n = (int) (world.getAffectionArray()[(ParticleType.values()[x]).index][ParticleType.values()[y].index].strength / 1.5 * 126 + 127);
				g.setColor(new Color(n, n, n));
				g.fillRect(squareSize + (y * squareSize) + 10, squareSize + (x * squareSize) + 10, squareSize, squareSize);
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	private int currentThreads = 5;

	public void disposeWorld() {
		world.dispose();
		world = new World(currentThreads, size);
		currentThreads++;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			disposeWorld();
//			world.dispose();
//			world = new World(world.getSize());
//			offsetY = getWidth();
//			offsetX = getHeight() / 2;
		}
		int speed = 5;
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			offsetX -= speed;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			offsetX += speed;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			offsetY -= speed;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			offsetY += speed;
		}
		if(e.getKeyCode() == KeyEvent.VK_E){
			world.dispose();
			System.exit(0);
		}
	}

	private Point dragDown = null;

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragDown != null) {
			Point diff = new Point(dragDown.x - e.getPoint().x, dragDown.y - e.getPoint().y);
			offsetX += diff.x / scale;
			offsetY += diff.y / scale;
			dragDown = e.getPoint();
			System.out.println(offsetX + ", " + offsetY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		dragDown = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragDown = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scale *= e.getUnitsToScroll() > 0 ? 1.03 : 0.97;
		if (scale >= 10) {
			scale = 10.0;
		} else if (scale <= 1.0 / WorldConstant.PARTICLE_DEFAULT_SIZE) {
			scale = 1.0 / WorldConstant.PARTICLE_DEFAULT_SIZE;
		}
		System.out.println(scale);
	}
}
