package utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import main.Main;

public class Menu extends ArrayList<String> {
	private Main main;
	private Font font;
	private ArrayList<RoundRectangle2D.Float> buttons;
	private ArrayList<Boolean> highlight;

	// CONSTRUCTORS
	public Menu(Main main) {
		this.main = main;
		font = main.getFont().deriveFont(50f);
		buttons = new ArrayList<RoundRectangle2D.Float>();
		highlight = new ArrayList<Boolean>();
	}

	// GETTERS&SETTERS
	public void setHighlight(Point p) {
		boolean ans=false;
		int i = select(p);
		for (int j = 0; j < buttons.size(); j++)
			if (i == j){
				ans|=!highlight.get(j);
				highlight.set(j, true);}
			else{
				ans|=highlight.get(j);
				highlight.set(j, false);}
		if (ans)
			main.setRefresh(1);
	}

	// METHODS
	public boolean add(String item) {
		super.add(item);
		buttons.clear();
		int xStep = main.WIDTH / 5;
		int yStep = main.HEIGHT / (size() * 2 + 1);
		for (int i = 0; i < size(); i++) {
			RoundRectangle2D.Float r = new RoundRectangle2D.Float(xStep, yStep * (2 * i + 1), xStep * 3, yStep, yStep,
					yStep);
			buttons.add(r);
			highlight.add(false);
		}
		return true;
	}

	private void centerString(Graphics2D g2d, RoundRectangle2D r, String s) {
		FontRenderContext frc = new FontRenderContext(null, true, true);

		Rectangle2D rec = font.getStringBounds(s, frc);
		int rWidth = (int) Math.round(rec.getWidth());
		int rHeight = (int) Math.round(rec.getHeight());
		int rX = (int) Math.round(rec.getX());
		int rY = (int) Math.round(rec.getY());

		int a = (int) ((r.getWidth() / 2) - (rWidth / 2) - rX);
		int b = (int) ((r.getHeight() / 2) - (rHeight / 2) - rY);

		g2d.drawString(s, (int) (r.getX() + a), (int) (r.getY() + b));
	}

	public int select(Point p) {
		for (RoundRectangle2D r : buttons)
			if (r.contains(p))
				return buttons.indexOf(r);
		return -1;
	}

	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(255, 255, 255, 128));
		g2d.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		for (int i = 0; i < buttons.size(); i++) {
			RoundRectangle2D r = buttons.get(i);
			if (highlight.get(i)) {
				g2d.setColor(new Color(0x3F7F7F7F, true));
				g2d.fill(r);
				g2d.setColor(Color.BLACK);
			}
			g2d.draw(r);
			centerString(g2d, r, get(i));
		}
	}
}