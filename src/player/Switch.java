package player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import main.Main;

public class Switch {
	private Color color;
	private boolean on;
	private Main main;
	private int xPos;
	private int yPos;
	private double size;
	private final int texSize = 90;

	// CONSTRUCTORS
	public Switch(Color color, int xPos, int yPos, double size, Main main) {
		this.color = color;
		this.on = false;
		this.main = main;
		this.xPos = xPos;
		this.yPos = yPos;
		this.size = size;
	}

	public Switch(Color color, boolean on, Main main) {
		this(color, 0, 0, 1, main);
	}

	// GETTERS&SETTERS
	public Color getColor() {
		return color;
	}

	public boolean isOn() {
		return on;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Rectangle getClickArea() {
		return new Rectangle(xPos, yPos, texSize, texSize);
	}

	public Point getPos() {
		return new Point(xPos + texSize / 2, yPos + texSize / 2);
	}

	// METHODS
	public void render(Graphics2D g2d) {
		g2d.drawImage(main.getTexture().getSwitch(color, on), xPos, yPos, (int) (texSize * size),
				(int) (texSize * size), null);
	}

}
