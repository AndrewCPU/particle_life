package net.andrewcpu.particle.render.panels;

import net.andrewcpu.particle.constants.WorldConstant;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.render.Renderer;
import net.andrewcpu.particle.render.Viewport;
import net.andrewcpu.particle.render.sliders.CustomParticleSliderPanel;
import net.andrewcpu.particle.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WorldCanvas extends JComponent implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	public static World world;

	public World getWorld() {
		return world;
	}

	public static List<CustomParticleSliderPanel> listeners = new ArrayList<>();


	public WorldCanvas(int size) {
		addKeyListener(this);
		world = new World(Math.max(20, size / 1000), size);
	}

	private int offsetX = 0;
	private int offsetY = 0;
	private double velocityX = 0;
	private double velocityY = 0;
	private double finalVelocityX = 0;
	private double finalVelocityY = 0;

	private long touchDown = 0;
	private Point initialDown = null;
	private double scale = 1.0;

	private net.andrewcpu.particle.render.Renderer renderer = new Renderer();
	private List<Long> times = new ArrayList<>();

	@Override
	public void paint(Graphics g) {
		finalVelocityX *= 0.94;
		finalVelocityY *= 0.94;
		offsetX -= finalVelocityX;
		offsetY -= finalVelocityY;
		Viewport viewport = new Viewport(new Rectangle(new Point((int) offsetX, (int) offsetY), new Dimension((int) (getWidth()), (int) (getHeight()))), scale);
		BufferedImage rendered = renderer.render(viewport, world);
		g.drawImage(rendered, 0, 0, getWidth(), getHeight(), null);

		int squareSize = 15;
		for (int x = 0; x < ParticleType.values().length; x++) {
			g.setColor(ParticleType.values()[x].getColor());
			g.fillRect(squareSize + (x * squareSize) + 10, 10, squareSize, squareSize);
			g.fillRect(10, squareSize + (x * squareSize) + 10, squareSize, squareSize);
			for (int y = 0; y < ParticleType.values().length; y++) {
				int n = (int) (world.getAffectionArray()[(ParticleType.values()[x]).index][ParticleType.values()[y].index].strength / 1.5 * 126 + 127);
				g.setColor(new Color(255 - n, n, 0));
				g.fillRect(squareSize + (y * squareSize) + 10, squareSize + (x * squareSize) + 10, squareSize, squareSize);
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}


	public void disposeWorld() {
		world.dispose();
		try{
			Thread.sleep(500);
			world = new World(world.getThreads(), world.getSize());
			listeners.forEach(CustomParticleSliderPanel::updateValues);
		}catch (Exception e){e.printStackTrace();}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			disposeWorld();
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			world.dispose();
			System.exit(0);
		}
	}

	public void dispose() {
		world.dispose();
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
			double currentX = e.getX();
			double currentY = e.getY();
			double currentTime = System.currentTimeMillis();
			double timeElapsed = currentTime - touchDown + 0.01;
			velocityX = (currentX - dragDown.x) / timeElapsed;
			velocityY = (currentY - dragDown.y) / timeElapsed;
			if(Double.isNaN(velocityX)){
				velocityX = 0;
			}
			if(Double.isNaN(velocityY)){
				velocityY = 0;
			}
			dragDown = e.getPoint();

			touchDown = (long) currentTime;
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
		touchDown = System.currentTimeMillis();
		initialDown = e.getPoint();
		finalVelocityX = 0;
		finalVelocityY = 0;
		velocityX =0;
		velocityY = 0;
		this.requestFocusInWindow();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(System.currentTimeMillis() - touchDown < 500 ){
			double distance = e.getPoint().distance(initialDown);
			finalVelocityX = velocityX * distance * scale;
			finalVelocityY = velocityY * distance * scale;
			if(finalVelocityX > 300){
				finalVelocityX = 300;
			}
			if(finalVelocityX < -300){
				finalVelocityX = -300;
			}
			if(finalVelocityY > 300){
				finalVelocityY = 300;
			}
			if(finalVelocityY < -300){
				finalVelocityY = -300;
			}
		}
		dragDown = null;
		initialDown = null;
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
