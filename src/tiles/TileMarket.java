package tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;

public class TileMarket extends ArrayList<Tile>{
	private Main main;
	private boolean refresh = true;

	public TileMarket(Main main) {
		this.main = main;
	}

	public boolean populate() {
		while (size() < main.getPlayerController().getNumberOfPlayer()) {
			Tile drawnTile = main.getTileStack().drawNextTile();
			if (drawnTile == null)
				return false;
			add(drawnTile);
		}
		expose();
		return true;
	}

	public void populate(ArrayList<Tile> tiles) {
		for (Tile t : tiles) {
			this.add(t);
			t.setSize(1);
		}
		expose();
	}

	public boolean add(Tile tile) {
		tile.setSize(1);
		super.add(tile);
		expose();
		return true;
	}

	public void expose() {
		if (isEmpty())
			return;
		int stepX = Main.WIDTH / (Math.min(4, size() + 1));
		int stepY = (int) (Main.HEIGHT / 3 / (Math.ceil(size() / 3.) + 1.));
		for (int i = 0; i < size(); i++)
			get(i).setPos(new Point((i % 3 + 1) * stepX, (int) (Main.HEIGHT / 3 + (i / 3 + 1) * stepY)));
	}

	public boolean tick() {
		// if (isEmpty())
		// populate();
		boolean ans = false;
		for (int i = 0; i < size(); i++) {
			ans |= get(i).tick(false);
		}
		refresh |= ans;
		return ans;
	}

	public boolean isEmpty() {
		return (size() == 0);
	}

	public void render(Graphics2D g2d) {
		if (refresh) {
			refresh = false;
			//main.getTexture().setMarketDump(
			//		new BufferedImage(main.WIDTH, main.HEIGHT, BufferedImage.TYPE_INT_ARGB));
			//Graphics2D g=(Graphics2D)main.getTexture().getMarketDump().getGraphics();
			g2d.setColor(new Color(255, 255, 255, 128));
			g2d.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
			for (int i = 0; i < size(); i++)
				get(i).render(g2d);
		}
		//g2d.drawImage(main.getTexture().getMarketDump(),0,0,null);
	}

	public Tile select(int x, int y) {
		return select(x, y, true);
	}

	public Tile select(int x, int y, boolean remove) {
		for (Tile t : this)
			if (t.getClickArea().contains(x, y)) {
				if (remove)
					remove(t);
				refresh=true;
				main.setRefresh(1);
				t.setSize(1.2);
				return t;
			}
		return null;
	}

	public void removeByColor(Color color) {
		for (int i = size() - 1; i >= 0; i--)
			if (((TerminalTile) get(i)).getColor() == color)
				remove(i);
		refresh=true;
	}

	public int size() {
		int size = super.size();
		for (Tile t : this)
			if (t == null)
				size--;
		return size;
	}
	
	public void setRefresh(){
		refresh=true;
	}
	
	public Point getPosition(int i){
		int stepX = Main.WIDTH / 3;
		int stepY = (int) (Main.HEIGHT / 2);
		
		return new Point((i % 3 + 1) * stepX, stepY);
	}
}
