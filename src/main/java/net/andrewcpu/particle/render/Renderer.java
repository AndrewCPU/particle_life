package net.andrewcpu.particle.render;

import net.andrewcpu.particle.world.Particle;
import net.andrewcpu.particle.world.World;
import net.andrewcpu.particle.physics.Vector2d;

import java.awt.*;
import java.awt.image.BufferedImage;

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

		g.dispose();
		return canvas;
	}
}
