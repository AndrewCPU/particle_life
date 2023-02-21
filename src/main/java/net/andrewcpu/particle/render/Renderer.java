package net.andrewcpu.particle.render;

import net.andrewcpu.particle.world.Particle;
import net.andrewcpu.particle.world.World;
import net.andrewcpu.particle.physics.Vector2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import static net.andrewcpu.particle.constants.WorldConstant.PARTICLE_DEFAULT_SIZE;

public class Renderer {
	private BufferedImage frame;
	private BufferedImage getFrame(Viewport viewport) {
		if(frame == null || viewport.getWidth() != frame.getWidth() || viewport.getHeight() != frame.getHeight()){
			BufferedImage bufferedImage = new BufferedImage(viewport.getWidth(), viewport.getHeight(), BufferedImage.TYPE_INT_ARGB);
			frame = bufferedImage;
		}
		return frame;
	}
	DecimalFormat decimalFormat = new DecimalFormat("0.00");


	private void drawOverlays(Viewport viewport, World world, Graphics g){
		int height = 60;
		int maxCount = 10;
		int stepWidth = 10;
		int start = world.averageTimes.size() - 1;
		int end = world.averageTimes.size() - 1;
		start -= maxCount;
		if(start < 0){
			start = 0;
		}
		int xPosition = 200;
		int n = 0;
		int lastX = xPosition;
		int lastY = -1;
		for(int i = start; i<end; i++){
			if(lastY == -1){
				double d = world.averageTimes.get(i);
				xPosition += stepWidth * n;
				n++;
				lastX = xPosition;
				lastY = (int)(height - (d / 20.0 * height));
				continue;
			}
			double d = world.averageTimes.get(i);
			xPosition += stepWidth * n;
			int x = (int)(xPosition);
			int y = (int)(height - (d / 20.0 * height));
			g.setColor(Color.GREEN);
			g.drawLine(x, y, lastX, lastY);
			lastX = x;
			lastY = y;
			n++;
		}
		g.drawString("Physics Tick: " + decimalFormat.format(world.averageTimes.get(world.averageTimes.size() - 1)) + "ms", 200, height + 15);
		g.drawLine(200,height, 650, height);
		g.drawLine(200,height, 200, 0);
		g.drawLine(650,0, 650, height);
	}

	public BufferedImage render(Viewport viewport, World world) {
		BufferedImage canvas = getFrame(viewport);
		Graphics g = canvas.createGraphics();
		((Graphics2D)g).setBackground(new Color(0, 0, 0));
		g.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
		int topLeftXOfWorld = viewport.getRectangle().getLocation().x ;
		int topLeftYOfWorld = viewport.getRectangle().getLocation().y ;
		g.setColor(Color.white);
		int particleSize = (int)(PARTICLE_DEFAULT_SIZE);
		if(world.isDisposed()){
			g.dispose();
			return canvas;
		}
		for(Particle particle : world.getParticles()) {
			if(particle == null) continue;
			Vector2d point = particle.position;
			int particleRadius = (int) (particleSize * Math.pow(viewport.getScale(),1));
			int x = (int) ((point.getX() - topLeftXOfWorld + ((viewport.getWidth()/2))/viewport.getScale()) * viewport.getScale());
			int y = (int) ((point.getY() - topLeftYOfWorld + ((viewport.getHeight()/2))/viewport.getScale()) * viewport.getScale());
			if (x >= 0 && x < viewport.getWidth() && y >= 0 && y < viewport.getHeight()) {
				Color color = particle.type.getColor();
				g.setColor(color);
				g.fillOval(x - particleRadius, y - particleRadius, 2 * particleRadius, 2 * particleRadius);
			}
		}
		drawOverlays(viewport, world, g);
		g.dispose();
		return canvas;
	}
}
