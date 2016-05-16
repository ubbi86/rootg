package tiles;

import java.awt.Color;
import java.util.ArrayList;

import main.Main;

public class TerminalTileStock extends ArrayList<TerminalTile>{
	//private ArrayList<TerminalTile> tiles;
	//CONSTRUCTORS
	public TerminalTileStock(Main main) {
		//tiles = new ArrayList<TerminalTile>();
		for (int i = 11; i < 20; i++) {
			TerminalTile t = new TerminalTile(i, main);
			add(t);
		}
	}
	//GETTERS&SETTERS
	public void setSide(Side side){
		for (TerminalTile t:this)
			t.setSide((side==Side.BACK?Side.FRONT:Side.BACK));
	}

	public ArrayList<TerminalTile> getTerminalTiles(Color color) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		for (int i = 0; i < size(); i++) {
			TerminalTile t = get(i);
			if (t.getColor() == color) {
				ans.add(t);
			}
		}
		return ans;
	}

	public TerminalTile getTerminalTile(Color color, int speed) {
		for (TerminalTile t : this)
			if (t.getColor() == color && t.getSpeed() == speed)
				return t;
		return null;
	}

	public ArrayList<Integer> getTerminalTileNumbers(){
		ArrayList<Integer> ans=new ArrayList<Integer>();
		for (TerminalTile t : this)
			ans.add(t.getTileNumber());
		return ans;
	}

	public ArrayList<TerminalTile> getTerminalTiles(int speed) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		for (int i = 0; i < size(); i++) {
			TerminalTile t = get(i);
			if (t.getSpeed() == speed) {
				ans.add(t);
			}
		}
		return ans;
	}
}
