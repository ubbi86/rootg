package tiles;

import java.awt.Color;
import java.util.ArrayList;

import main.Main;

public class TerminalTileStock {
	private ArrayList<TerminalTile> tiles;

	public TerminalTileStock(Main main) {
		tiles = new ArrayList<TerminalTile>();
		for (int i = 11; i < 20; i++) {
			TerminalTile t = new TerminalTile(i, main);
			tiles.add(t);
		}
	}
	
	public void setSide(Side side){
		for (TerminalTile t:tiles)
			t.setSide((side==Side.BACK?Side.FRONT:Side.BACK));
	}

	public ArrayList<TerminalTile> getTerminalTiles(Color color) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		for (int i = 0; i < tiles.size(); i++) {
			TerminalTile t = tiles.get(i);
			if (t.getColor() == color) {
				ans.add(t);
			}
		}
		return ans;
	}

	public TerminalTile getTerminalTile(Color color, int speed) {
		for (TerminalTile t : tiles)
			if (t.getColor() == color && t.getSpeed() == speed)
				return t;
		return null;
	}

	public ArrayList<Integer> getTerminalTileNumbers(){
		ArrayList<Integer> ans=new ArrayList<Integer>();
		for (TerminalTile t : tiles)
			ans.add(t.getTileNumber());
		return ans;
	}

	public ArrayList<TerminalTile> getTerminalTiles(int speed) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		for (int i = 0; i < tiles.size(); i++) {
			TerminalTile t = tiles.get(i);
			if (t.getSpeed() == speed) {
				ans.add(t);
			}
		}
		return ans;
	}

	public void add(TerminalTile t) {
		tiles.add(t);
	}
}
