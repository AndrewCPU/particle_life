package net.andrewcpu.particle.render.panels;

import net.andrewcpu.particle.constants.WorldConstant;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.render.sliders.CustomSliderPanel;
import net.andrewcpu.particle.render.tabs.TabbedUI;
import net.andrewcpu.particle.util.ValueChangeEvent;

import javax.swing.*;
import java.awt.*;

public class OptionPane extends JComponent {
	private JSlider slider1, slider2, slider3, slider4, slider5;

	public OptionPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.2f), backgroundColor));
		JPanel padded = new JPanel();
		setFocusable(true);
		setBackground(backgroundColor);
		// Add other components here
		padded.add(createSlider("Drag", value -> {
			WorldConstant.DRAG = value / 100.0;
		}, (int) (WorldConstant.DRAG * 100.0)));
		padded.add(createSlider("Repelling Force", value -> {
			WorldConstant.REPELLING_MULTIPLIER = value / 100.0;
		}, (int) (WorldConstant.DRAG * 100.0)));

		padded.add(getPadding(50));
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(true);
		TabbedUI ui1 = new TabbedUI(tabbedPane);
		tabbedPane.setUI(ui1);
		int i = 0;
		for(ParticleType type: ParticleType.values()){
			tabbedPane.addTab(type.name().substring(0,1) + type.name().toLowerCase().substring(1), new ParticleTypeCustomizer(type, tabbedPane){
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(padded.getWidth(), padded.getHeight());
				}
			});
			ui1.setTabColor(i, type.getColor());
			i++;
		}
		padded.add(tabbedPane, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		setBackground(backgroundColor);
		padded.setBackground(backgroundColor);
		add(createPaddingPanel(10, 10), BorderLayout.NORTH);
		add(createPaddingPanel(10, 10), BorderLayout.SOUTH);
		add(createPaddingPanel(10, 10), BorderLayout.WEST);
		add(createPaddingPanel(10, 10), BorderLayout.EAST);
		add(padded, BorderLayout.CENTER);
		setFocusable(true);
	}

	public JComponent getPadding(int padd) {
		JComponent component = new JPanel();
		component.setPreferredSize(new Dimension(getWidth(), padd));
		component.setSize(new Dimension(getWidth(), padd));
		return component;
	}

	public static Color backgroundColor = new Color(24, 24, 35);
	private JPanel createPaddingPanel(int width, int height) {
		JPanel paddingPanel = new JPanel();
		paddingPanel.setLayout(new BoxLayout(paddingPanel,BoxLayout.PAGE_AXIS));
		paddingPanel.setPreferredSize(new Dimension(width, height));
		paddingPanel.setBackground(backgroundColor);
		return paddingPanel;
	}
	private JPanel createSlider(String labelText, ValueChangeEvent event, int min, int max, int value) {
		CustomSliderPanel sliderPanel = new CustomSliderPanel(min, max, value, event, labelText);
		return sliderPanel;
	}

	private JPanel createSlider(String labelText, ValueChangeEvent event, int value) {
		return createSlider(labelText, event, 0, 100, value);
	}

	private void resizeLabel(JLabel label) {
		label.setPreferredSize(new Dimension(150, 20));
		label.setSize(new Dimension(150, 20));
	}
}
