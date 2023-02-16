package net.andrewcpu.particle.render.sliders;

import net.andrewcpu.particle.render.panels.GamePanel;
import net.andrewcpu.particle.render.panels.OptionPane;
import net.andrewcpu.particle.util.ValueChangeEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class CustomSliderPanel extends JPanel {
	private CustomSlider slider;
	public CustomSliderPanel(int min, int max, int value, ValueChangeEvent event, String text) {
//		super(layout, isDoubleBuffered);
		setLayout(new BorderLayout());
		setBackground(OptionPane.backgroundColor);
		slider = new CustomSlider();
		slider.setMinimum(min);
//		slider.setBackground(OptionPane.backgroundColor);
		slider.setMaximum(max);
		slider.setValue(value);
		slider.setUI(new CustomSliderUI(slider));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				event.onChange(slider.getValue());
			}
		});
		JLabel label = new JLabel(text);
		resizeLabel(label);
		label.setFont(GamePanel.mainFont);
		label.setForeground(Color.white);
		add(label, BorderLayout.WEST);
		add(slider, BorderLayout.CENTER);

	}

	public void setThumbColor(Color color){
		((CustomSliderUI)slider.getUI()).setThumbColor(color);
	}

	private void resizeLabel(JLabel label) {
		label.setPreferredSize(new Dimension(150, 20));
		label.setSize(new Dimension(150, 20));
	}
}
