package net.andrewcpu.particle.render.tabs;

import net.andrewcpu.particle.render.panels.GamePanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.HashMap;

public class TabbedUI extends BasicTabbedPaneUI {
	private Color tabBackgroundColor = Color.LIGHT_GRAY;
	private Color tabForegroundColor = Color.BLACK;
	private Font tabFont = new Font("Arial", Font.PLAIN, 12);
	private JTabbedPane tabbedPane;
	public TabbedUI(JTabbedPane tabbedPane) {
		tabbedPane.setFont(GamePanel.thinFont);
		this.tabbedPane = tabbedPane;
		UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
		UIManager.getDefaults().put("TabbedPane.selectedForeground", Color.white);
		UIManager.getDefaults().put("TabbedPane.foreground", new Color(255,255,255,255));
		UIManager.getDefaults().put("TabbedPane.tabInsets", new Insets(0,100,0,100));
		UIManager.getDefaults().put("TabbedPane.font", GamePanel.thinFont);

	}

	@Override
	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		return 75;
	}

	@Override
	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
		return 45;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		tabAreaInsets.left = 0;
		selectedTabPadInsets = new Insets(0, 0, 0, 0);
		tabInsets = new Insets(0, 0, 0, 0);

	}

	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {

	}

	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(colorHashMap.containsKey(tabIndex) ? colorHashMap.get(tabIndex).darker() : tabBackgroundColor);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.setColor(fillColor);
//		int x = 0;
//		int y = 0;
		int width = w;
		int height = h;
//		RoundRectangle2D rr = new RoundRectangle2D.Float(x, y, width, height, 25, 25);
//		g2.fill(rr);
		g2.fillRoundRect(x, y, width, height, 20, 20);
		g2.fillRect(x, y+10, w , h);
	}

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		if(isSelected){
			g.setColor(Color.white);
			((Graphics2D)g).setStroke(new BasicStroke(2.0f));
			((Graphics2D)g).drawRoundRect(x, y, w, h+10, 20, 20);
//			super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
		}
	}

	@Override
	protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
		super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
	}

	@Override
	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {

		super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

	}

	@Override
	protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
		Color c = (colorHashMap.containsKey((tabIndex)) ? colorHashMap.get(tabIndex) : tabBackgroundColor);
//		g.setFont();
		g.setColor(Color.white);
//		g.drawString(title, textRect.x , textRect.y + (textRect.height));
		g.setFont(GamePanel.mainFont);
		super.paintText(g, tabPlacement, font, metrics, tabIndex, title, new Rectangle(textRect.x,textRect.y,100,50), isSelected);
	}
	private HashMap<Integer, Color> colorHashMap = new HashMap<>();
	public void setTabColor(int index, Color color) {
		colorHashMap.put(index, color);
	}

}
