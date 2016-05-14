package tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import main.Texture;

public class Tile extends BasicTile implements Cloneable {
	private int xCell;
	private int yCell;
	private int angle = 0;
	private int renderAngle = 0;
	private Main main;
	private int xPos;
	private int yPos;
	private double size;
	private double flip = 1;
	private double flipStep = 0.75;
	private int mask = 0xFFFFFF;
	private boolean fixColor;

	public Tile(int tileNumber, double size, Side side, Main main) {
		super(tileNumber, side, main.getTexture(), main.getIntLoader());
		this.main = main;
		this.size = size;
	}

	public Tile(int tileNumber, double size, Main main) {
		super(tileNumber, (main.side == Side.FRONT ? Side.BACK : Side.FRONT), main.getTexture(), main.getIntLoader());
		this.main = main;
		this.size = size;
	}

	public Tile(int xPos, int yPos, int tileNumber, double size, Main main) {
		this(tileNumber, size, main);
		Point p = main.getGrid().nearest(new Point(xPos, yPos));
		this.xCell = p.x;
		this.yCell = p.y;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public Tile(Point p, int tileNumber, double size, Main main) {
		this(tileNumber, size, main);
		this.xCell = p.x;
		this.yCell = p.y;
		Point pos = main.getGrid().grid(xCell, yCell);
		xPos = pos.x;
		yPos = pos.y;
	}

	public int getXCell() {
		return xCell;
	}

	public int getYCell() {
		return yCell;
	}

	public Point getCell() {
		return new Point(xCell, yCell);
	}

	public Point getPos() {
		return new Point(xPos, yPos);
	}

	public int getAngle() {
		int reducedAngle = angle;
		while (reducedAngle < 0)
			reducedAngle += 360;
		//angle=reducedAngle;
		
		return reducedAngle % 360;
	}
	
	public int getRenderAngle() {
		int reducedAngle = renderAngle;
		while (reducedAngle < 0)
			reducedAngle += 360;
		//angle=reducedAngle;
		
		return reducedAngle % 360;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public void rotate(int ticks) {
		this.angle += (ticks * 60) % 360;
	}

	public void render(Graphics2D g2d) {
		boolean rotating=(renderAngle != angle);
		g2d.translate(xPos, yPos);
		g2d.rotate(Math.toRadians((rotating?renderAngle:renderAngle%60)));
		double scale = Main.TILESCALE * size;
		g2d.drawImage(maskTexture((rotating?super.fetchTexture():fetchTexture())), (int) (-Texture.TILEWIDTH / 2 * scale * flip),
				(int) (-Texture.TILEHEIGHT / 2 * scale), (int) (Texture.TILEWIDTH * scale * flip),
				(int) (Texture.TILEHEIGHT * scale), null);

		g2d.rotate(Math.toRadians(-(rotating?renderAngle:renderAngle%60)));
		g2d.translate(-xPos, -yPos);

	}

	public void renderShadow(Graphics2D g2d) {
		int offset = (int) ((size - 1) * Texture.TILEWIDTH * Main.TILESCALE);
		double scale = Texture.TILEWIDTH * Main.TILESCALE * size;
		double slope = 0.01 * size;
		double newX = xPos + slope * (xPos - Main.WIDTH / 2) + offset;
		double newY = yPos + slope * (yPos - Main.HEIGHT / 2) + offset;
		g2d.translate(newX, newY);
		g2d.rotate(Math.toRadians(renderAngle));
		g2d.drawImage(
				maskTexture(main.getTexture().getShadow(side),
						0x00FFFFFF | ((int) (0x9F * (2 - size) * (2 - size)) << 24)),
				(int) (-scale * flip / 2), (int) (-scale / 2), (int) (scale * flip), (int) (scale), null);
		g2d.rotate(Math.toRadians(-renderAngle));
		g2d.translate(-newX, -newY);
	}

	public void setCell(Point p) {
		xCell = (int) p.getX();
		yCell = (int) p.getY();
		Point pos = main.getGrid().grid(xCell, yCell);
		xPos = (int) pos.getX();
		yPos = (int) pos.getY();
	}

	public void setPos(Point p) {
		xPos = (int) p.getX();
		yPos = (int) p.getY();
		Point cell = main.getGrid().nearest(p);
		xCell = (int) cell.getX();
		yCell = (int) cell.getY();
	}

	public int getTileNumber() {
		return tileNumber;
	}

	public boolean tick() {
		return tick(true);
	}

	public boolean tick(boolean dropping) {
		// if (renderAngle == angle) {
		// angle=getAngle();
		// renderAngle = angle;
		// }
		boolean ans = false;
		Tile activeTile = main.getActiveTile();
		if (renderAngle != angle) {
			ans |= (this != activeTile);
			if (Math.abs(renderAngle - angle) < 10)
				renderAngle = angle;
			else
				renderAngle += (angle - renderAngle) / 4;
		}
		
		if (dropping && size > 1 && this != activeTile) {
			ans = true;
			double targetSize = (this != activeTile ? 1 : 1.2);
			double dropspeed = (size - targetSize) / 4;
			size -= dropspeed;
			if (size - targetSize < 0.1)
				size = targetSize;
		}
		if (flip < 1) {
			ans |= (this != activeTile);
			flip *= flipStep;
			if (flip < 0.07){
				flipStep =1.5;
				setSide(side == Side.BACK?Side.FRONT:Side.BACK);
			}
			if (flip > 0.9) {
				flip = 1;
				flipStep = 0.75;
			}
		}

		if (mask != 0xFFFFFFFF&&!fixColor) {
			ans |= (this != activeTile);
			mask = 0xFF000000 + ((mask & 0xFF0000) << 1 & 0xFF0000) 
					+ ((mask & 0xFF00) << 1 & 0xFF00)
					+ ((mask & 0xFF) << 1 & 0xFF);
			mask |= 0x00010101;
		}
		return ans;
	}

	public void setSize(double size) {
		this.size = size;

	}

	public void flip() {
		flip = 0.99;
	}

	public Interface getInterface(int side) {
		return interf[originalSide(side)];
	}

	public Interface[] getInterf() {
		Interface[] interfOut = new Interface[6];
		for (int i = 0; i < 6; i++)
			interfOut[i] = getInterface(i);
		return interfOut;
	}

	public ArrayList<Integer> whereIsThis(Interface i) {
		ArrayList<Integer> ans = super.whereIsThis(i);
		ArrayList<Integer> newAns = new ArrayList<Integer>();
		for (Integer j : ans) {
			newAns.add((j + (getAngle() / 60)) % 6);
		}
		return newAns;
	}
	
	public ArrayList<Integer> listSides(IO io) {
		ArrayList<Integer> ans = new ArrayList<Integer>();
		for (int i=0;i<6;i++) {
			if(getInterface(i).getIo()==io||getInterface(i).getIo()==IO.BOTH)
				ans.add(i);
		}
		return ans;
	}

	public Rectangle clickArea() {
		return new Rectangle((int) (xPos - Texture.TILEWIDTH * size / 2), (int) (yPos - Texture.TILEHEIGHT * size / 2),
				(int) (Texture.TILEWIDTH * size), (int) (Texture.TILEHEIGHT * size));
	}

	public ArrayList<Integer> getGates(IO dir) {
		ArrayList<Integer> ans = super.getGates(dir);
		ArrayList<Integer> newAns = new ArrayList<Integer>();
		for (Integer j : ans) {
			newAns.add((j + getAngle() / 60) % 6);
		}
		return newAns;
	}

	public ArrayList<Integer> getGates() {
		return getGates(IO.BOTH);
	}

	public static int opposite(int sideIn) {
		return (sideIn + 3) % 6;
	}

	public int originalSide(int rotatedSide) {
		return (rotatedSide + (360 - getAngle()) / 60) % 6;
	}

	private BufferedImage maskTexture(BufferedImage image) {
		if (mask == 0xFFFFFF)
			return image;
		return maskTexture(image, mask);
	}

	private BufferedImage maskTexture(BufferedImage image, int mask) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < imagePixels.length; i++)
			imagePixels[i] &= mask;
		BufferedImage maskedTex = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		maskedTex.setRGB(0, 0, width, height, imagePixels, 0, width);
		return maskedTex;
	}

	public void setMask(Color color, boolean fixColor) {
		this.fixColor=fixColor;
		mask = (fixColor?0xFF9F9F9F:0xFF000000)|(
					(color.getRed() << 15) 
				| 	(color.getGreen() << 7) 
				| 	color.getBlue());
	}
	
	public void setMask(Color color){
		setMask(color, false);
	}

	@Override
	public Tile clone() throws CloneNotSupportedException {
		return (Tile) super.clone();
	}

	public void setFixColor(boolean fixColor) {
		this.fixColor = fixColor;
	}

	@Override
	protected BufferedImage fetchTexture() {
		return texture.getTile(this);
	}

}
/*
 * private BufferedImage drawDark(BufferedImage image) { int width =
 * image.getWidth(); int height = image.getHeight(); int[] imagePixels =
 * image.getRGB(0, 0, width, height, null, 0, width); for (int i = 0; i <
 * imagePixels.length; i++){ imagePixels[i] &= 0xaf000000; } BufferedImage
 * imageOut = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
 * imageOut.setRGB(0, 0, width, height, imagePixels, 0, width); return imageOut;
 * }
 */
