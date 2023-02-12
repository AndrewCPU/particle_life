package org.example.render;

import org.example.Particle;
import org.example.Vector2d;
import org.example.World;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.example.WorldConstant.PARTICLE_DEFAULT_SIZE;

public class Renderer {
	private BufferedImage frame;
	public Renderer() {

	}

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
		((Graphics2D)g).setBackground(new Color(13, 4,3));
		g.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
//		Point middleOfScreen = viewport.getRectangle().getLocation();
		int viewportScaledWidth = (int)(viewport.getWidth() * viewport.getScale());
		int viewportScaledHeight = (int)(viewport.getHeight() * viewport.getScale());
		Point middleOfScreen = new Point(viewport.getRectangle().getLocation().x + viewport.getWidth() / 2, viewport.getRectangle().getLocation().y + viewport.getHeight() / 2);
		int topLeftXOfWorld = viewport.getRectangle().getLocation().x ;
		int topLeftYOfWorld = viewport.getRectangle().getLocation().y ;
		g.setColor(Color.white);
		int particleSize = (int)(PARTICLE_DEFAULT_SIZE);

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
				if(viewport.getScale() >= 1.0){
					double rings = 2;
					if(viewport.getScale() >= 1.25){
						rings = 3;
					}
					if(viewport.getScale() >= 1.5){
						rings = 4;
					}
					for(double circleY = particleRadius; circleY<particleRadius * 2.0; circleY+=(particleRadius / rings)){
						g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(50*((particleRadius * 2 - circleY) / (1.0 * particleRadius)))));
						g.fillOval(x - (int)circleY, y - (int)circleY, (int)(circleY*2), (int)(circleY*2));
					}
				}

			}
		}

//		Point topLeftXOfViewport = viewport.getRectangle().getLocation();
//		Point bottomRightXOfViewport = viewport.getRectangle()

//		world.getParticles().stream().forEach(physicsObject -> {
//			Point centerOnScreen = new Point((int) (physicsObject.getPoint().getX() - viewport.getRectangle().getX() + (viewport.getWidth() / 2)), (int) (physicsObject.getPoint().getY() - viewport.getRectangle().getY() + (viewport.getHeight() / 2)));
//			int size = 10;
//			if (centerOnScreen.x < -size || centerOnScreen.y < -size || centerOnScreen.x > viewport.getWidth() + size || centerOnScreen.getY() > viewport.getHeight() + size) {
//				return;
//			}
//			g.setColor(physicsObject.getType().getColor());
//			g.fillOval((int) Math.floor(centerOnScreen.x - (size / 2.0)), (int) Math.floor(centerOnScreen.y - (size / 2.0)), 10,10);
//		});
//		g.dispose();
		return canvas;
	}
}
