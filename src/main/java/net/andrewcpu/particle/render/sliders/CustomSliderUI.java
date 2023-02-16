package net.andrewcpu.particle.render.sliders;
import java.awt.*;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class CustomSliderUI extends BasicSliderUI {
	private Color trackColor = Color.BLACK;
	private Color thumbColor = Color.RED;

	public CustomSliderUI(JSlider slider) {
		super(slider);
	}


	@Override
	public void paintFocus(Graphics g) {

	}

	@Override
	public void paintTrack(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int value = slider.getValue();
		int thumbWidth = thumbRect.width;
		int thumbHeight = thumbRect.height;
		int sliderHeight = thumbHeight / 3;
		int trackWidth = trackRect.width - thumbWidth;
		int trackLeft = trackRect.x + thumbWidth;
		int range = slider.getMaximum() - slider.getMinimum();
		int fillWidth = (value - slider.getMinimum()) * trackWidth / range;
		g.setColor(thumbColor.darker());
		g.fillRoundRect(trackLeft, trackRect.y + (thumbHeight / 2 - (sliderHeight / 2)), fillWidth, sliderHeight, sliderHeight, sliderHeight);
		g.setColor(thumbColor.darker().darker().darker());
		g.fillRoundRect(trackLeft+fillWidth, trackRect.y + (thumbHeight / 2 - (sliderHeight / 2)), trackRect.width - (trackLeft + fillWidth), sliderHeight, sliderHeight, sliderHeight);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	@Override
	public void paintThumb(Graphics g) {
		Rectangle knobBounds = thumbRect;
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(thumbColor);
		g.fillOval(knobBounds.x, knobBounds.y, knobBounds.height, knobBounds.height);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

	}

	public void setTrackColor(Color trackColor) {
		this.trackColor = trackColor;
	}

	public void setThumbColor(Color thumbColor) {
		this.thumbColor = thumbColor;
	}
}
