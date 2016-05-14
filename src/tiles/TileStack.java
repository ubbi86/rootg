package tiles;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import main.Main;
import main.Texture;

public class TileStack {
	private static final int[] defaultStack = {27, 29, 37, 44, 53, 43, 46, 35, 50, 65, 62, 36, 59};
	private ArrayList<Tile> tileStack;
	private int lastSpecialTile = 19;
	private Random r;
	private Main main;

	public TileStack(Main main) {
		this.main=main;
		tileStack = new ArrayList<Tile>();
		makeStack();
	}
	public void makeStack(){
		makeStack(false);
	}
	public void makeStack(boolean notRandom){
		ArrayList<Integer> rndArray = new ArrayList<Integer>();
		if (notRandom)
			for (int i:defaultStack)
			rndArray.add(i);
		r = new Random();
		int rnd;
		while (rndArray.size() < Main.TILENUMBER - lastSpecialTile - 1) {
			rnd = r.nextInt(Main.TILENUMBER);
			if (!rndArray.contains(rnd) && rnd > lastSpecialTile)
				rndArray.add(rnd);
		}
		int count = 0;
		if (notRandom)
			Collections.reverse(rndArray);
		for (Integer i : rndArray) {
			tileStack.add(new Tile(Texture.TILEWIDTH*3/2 + count, Main.HEIGHT*5/2  - count*3, i,
					1. + count / 50., main));
			count++;
		}
	}
	
	public void setSide(Side side){
		side=(side==Side.BACK?Side.FRONT:Side.BACK);
		for (Tile t:tileStack)
			t.setSide(side);
	}
	
	public Tile drawNextTile() {
		if (tileStack.isEmpty()) {
			return null;
		}
		Tile tile = tileStack.get(tileStack.size() - 1);
		tile.flip();
		// activeTile.setPos(new Point(mX,mY));
		// activeTile.setBack(BACK);
		tileStack.remove(tile);
		tile.setSize(1.2);
		return tile;
	}

	public void render(Graphics2D g2d) {
		if (main.getTc().getTilesOnTable()==0)
			return;
		if (tileStack.size() == 0)
			return;
		for (int i = 0; i < tileStack.size(); i++)
			tileStack.get(i).renderShadow(g2d);
		for(int i=0; i<tileStack.size();i++)
			tileStack.get(i).render(g2d);
	}

	public Rectangle getStackPos() {
		if (tileStack.size() > 0)
			return tileStack.get(tileStack.size() - 1).clickArea();
		return new Rectangle();
	}
	public Point getCenter() {
			Rectangle r= getStackPos();
			return new Point(r.x+r.width/2,r.y+r.height/2);
	}
	
	public Tile getTile(int tileNumber)
	{
		for (Tile t:tileStack)
			if (t.getTileNumber()==tileNumber)
				return t;
		return null;
	}
	
	public int size(){
		return tileStack.size();
	}
}
