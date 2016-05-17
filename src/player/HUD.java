package player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import main.Main;
import tiles.Side;
import tiles.Tile;

public class HUD {
	private double size;
	private Player player;
	private Tile powerSolder;
	private Font font;
	private int yPos;
	private int xPos = 65;
	private Color color;

	// CONSTRUCTORS
	public HUD(Player player, Main main) {
		this.player = player;
		size = 1;
		powerSolder = new Tile(7, 0.5 * size, main);
		powerSolder.setSide(Side.FRONT);
		font = main.getFont();
		color=player.getColor();
	}

	// GETTERS&SETTERS
	public void setSize(double size) {
		this.size = size;
	}

	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}
	
	public Player getPlayer() {
		return player;
	}

	public Rectangle getClickArea() {
		return new Rectangle((int) (xPos - 5 * size), (int) (yPos - 5 * size), (int) (50 * size), (int) (50 * size));
	}

	public Rectangle getPowerSolderClickArea() {
		if (player.hasPowerSolder())
			return powerSolder.getClickArea();
		return new Rectangle();
	}

	// METHODS
	public void render(Graphics2D g2d) {
		yPos -= 40 * (size - 1);

		Font bigFont = font.deriveFont((float) (size * 20f));
		Font smallFont = font.deriveFont((float) (size * 10f));
		g2d.setFont(bigFont);
		g2d.setColor((player.getChargeTokens() == 0 ? Color.YELLOW : new Color(0, 0, 0, 128)));
		g2d.fillRoundRect((int) (xPos - 5 * size), (int) (yPos - 5 * size), (int) (50 * size), (int) (50 * size),
				(int) (25 * size), (int) (25 * size));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3 - i; j++)
				g2d.fillRoundRect((int) (xPos - 22 - j * 14 * size), (int) (yPos + (-4 + i * 15) * size),
						(int) (15 * size), (int) (15 * size), (int) (7 * size), (int) (7 * size));

		g2d.setColor(color);
		g2d.fillRoundRect(xPos, yPos, (int) (40 * size), (int) (40 * size), (int) (20 * size), (int) (20 * size));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3 - i; j++)
				g2d.fillRoundRect((int) (xPos - 20 - j * 13 * size), (int) (yPos + (-2 + i * 15) * size),
						(int) (12 * size), (int) (12 * size), (int) (6 * size), (int) (6 * size));

		g2d.setColor((color==Color.BLUE?Color.WHITE:Color.BLACK));
		g2d.drawString(Integer.toString(player.getChargeTokens()), (int) (10 + xPos + size), (int) (size * 30) + yPos);
		
		g2d.setFont(smallFont);
		for (int i = 0; i < 3; i++)
			g2d.drawString(Integer.toString(player.getCircDelay(3 - i)), (int) (xPos + size - 18),
					(int) (yPos + (+8 + i * 15) * size));

		if (player.hasPowerSolder()) {
			powerSolder.setPos(new Point(xPos + 85, yPos + 25));
			powerSolder.setSize(size * 0.5);
			powerSolder.render(g2d);
		}
	}

}
