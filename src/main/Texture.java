package main;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tiles.Side;
import tiles.Tile;

public class Texture {
	private BufferedImage[] spritesheetF = new BufferedImage[3];
	private BufferedImage[] spritesheetB = new BufferedImage[3];
	protected BufferedImage background;
	private BufferedImage shadowF;
	private BufferedImage shadowB;
	private BufferedImage extraTex;
	private BufferedImage marketDump;

	private static final int HEIGHT = 1344;
	private static final int WIDTH = 1088;
	public static final int ROWS = 9;
	public static final int COLS = 8;
	public static final int TILEHEIGHT = 136;
	public static final int TILEWIDTH = 136;

	public Texture() {
		try {
			background = ImageIO.read(getClass().getResourceAsStream("/background.jpg"));
			for (int i = 0; i < 3; i++) {
				spritesheetF[i] = ImageIO.read(getClass().getResourceAsStream("/textureF" + i + ".png"));
				spritesheetB[i] = ImageIO.read(getClass().getResourceAsStream("/textureB" + i + ".png"));
			}
			extraTex = ImageIO.read(getClass().getResourceAsStream("/extra.png"));
			createShadows();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getTile(int col, int row) {
		return getTile(col, row, Side.FRONT);
	}

	public BufferedImage getTile(int col, int row, Side side) {
		return (side == Side.BACK ? spritesheetB[0] : spritesheetF[0]).getSubimage(col * WIDTH / COLS,
				row * HEIGHT / ROWS, TILEWIDTH, TILEHEIGHT);
	}

	public BufferedImage getTile(Tile t) {
		Side side = t.getSide();
		int col = t.getTileNumber() % Texture.COLS;
		int row = t.getTileNumber() / Texture.COLS;
		int angle = t.getAngle();
		int sheet = (angle / 60) % 3;
		BufferedImage image = (side == Side.BACK ? spritesheetB[sheet] : spritesheetF[sheet])
				.getSubimage(col * WIDTH / COLS, row * HEIGHT / ROWS, TILEWIDTH, TILEHEIGHT);
		if (angle > 120) {
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			tx.translate(-image.getWidth(null), -image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);
		}
		return image;
	}

	private void drawTrasparent(BufferedImage image)// , BufferedImage mask)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < imagePixels.length; i++)
			imagePixels[i] &= 0xafffffff;
		// BufferedImage imageOut = new
		// BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, imagePixels, 0, width);
	}

	private void createShadows() {
		BufferedImage image = getTile(0, 0);
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < imagePixels.length; i++) {
			imagePixels[i] &= 0x5F000000;
		}
		shadowF = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		shadowF.setRGB(0, 0, width, height, imagePixels, 0, width);

		image = getTile(0, 0, Side.BACK);
		imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < imagePixels.length; i++) {
			imagePixels[i] &= 0xaf000000;
		}
		shadowB = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		shadowB.setRGB(0, 0, width, height, imagePixels, 0, width);
	}

	public BufferedImage getShadow(Side side) {
		return (side == Side.BACK ? shadowB : shadowF);
	}

	public BufferedImage getSwitch(Color color, boolean on) {
		int row = (color == Color.RED ? 0 : (color == Color.GREEN ? 1 : 2));
		int col = (on ? 0 : 1);
		return (extraTex.getSubimage(col * 90, row * 90, 90, 90));
	}

	public BufferedImage getSpark(boolean one) {
		int row = 3;
		int col = (one ? 1 : 0);
		return (extraTex.getSubimage(col * 90, row * 90, 90, 90));
	}

	public BufferedImage getSpark() {
		int row = 3;
		int col = 2;
		return (extraTex.getSubimage(col * 90, row * 90, 90, 90));
	}

	public BufferedImage getMarketDump() {
		return marketDump;
	}

	public void setMarketDump(BufferedImage marketDump) {
		this.marketDump = marketDump;
	}

}
