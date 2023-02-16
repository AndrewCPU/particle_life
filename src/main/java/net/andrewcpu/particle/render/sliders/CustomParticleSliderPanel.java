package net.andrewcpu.particle.render.sliders;

import net.andrewcpu.particle.render.panels.GamePanel;
import net.andrewcpu.particle.render.panels.WorldCanvas;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.util.ValueChangeEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;

public class CustomParticleSliderPanel extends JPanel {
	private CustomSlider slider;
	private ParticleType type;
	private ParticleType target;
	private JLabel valueLabel;
	private DecimalFormat format = new DecimalFormat("0.00");


	public CustomParticleSliderPanel(int min, int max, int value, ValueChangeEvent event, String text, ParticleType type, ParticleType target) {
		this.type = type;
		this.target = target;
		setLayout(new BorderLayout());
		setOpaque(false);
		slider = new CustomSlider();
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setValue(value);
		slider.setUI(new CustomSliderUI(slider));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				event.onChange(slider.getValue());
				valueLabel.setText(format.format(slider.getValue() * 1.0 / max));
			}
		});
		WorldCanvas.listeners.add(this );

		valueLabel = new JLabel(format.format(value * 1.0 / max));
		valueLabel.setFont(GamePanel.thinFont);
		valueLabel.setForeground(Color.white);
		resizeValue(valueLabel);

		JLabel label = new JLabel(text);
		resizeLabel(label);
		label.setFont(GamePanel.thinFont);
		label.setForeground(Color.white);

		add(label, BorderLayout.WEST);
		add(slider, BorderLayout.CENTER);
		add(valueLabel, BorderLayout.EAST);
	}

	public void updateValues() {
		slider.setValue((int)(WorldCanvas.world.getAffectionArray()[type.index][target.index].strength * 100.0));

	}

	public void setThumbColor(Color color){
		((CustomSliderUI)slider.getUI()).setThumbColor(color);
	}

	private void resizeLabel(JLabel label) {
		label.setPreferredSize(new Dimension(125, 20));
		label.setSize(new Dimension(125, 20));
	}
	private void resizeValue(JLabel label) {
		label.setPreferredSize(new Dimension(50, 20));
		label.setSize(new Dimension(50, 20));
	}
}
