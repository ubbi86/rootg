package tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.Main;
import player.Player;
import player.PlayerController;
import utility.SparkController;
import utility.WireChaser;

@SuppressWarnings("serial")
public class TileController extends ArrayList<Tile> {
	private Tile[][] placedTiles = new Tile[32][32];
	private Main main;
	private int tilesOnTable;
	private ArrayList<Boolean> refresh;
	private ArrayList<Boolean> dropped;
	private TerminalTile[] core = new TerminalTile[6];

	public TileController(Main main) {
		this.main = main;
		refresh = new ArrayList<Boolean>();
		dropped = new ArrayList<Boolean>();
		placedTiles = new Tile[256][256];
		tilesOnTable = 0;
	}

	public void init(boolean def) {
		Tile coreTile = (main.side == Side.BACK ? main.coreB : main.coreF);
		// Interface[] interfaces = new Interface[6];
		// for (int i = 0; i < 6; i++) {
		// interfaces[i] = new Interface(IO.INPUT, Device.CORE);
		// }
		// tile.setInterfaces(interfaces);
		Point center = main.getGrid().getCenter();
		add(coreTile, center);
		if (def) {
			Point[] corePos = new Point[6];
			corePos = main.getGrid().neightbours(center);
			TerminalTileStock ts = new TerminalTileStock(main);
			for (int i = 0; i < 3; i++) {
				TerminalTile tile = ts.getTerminalTiles(Color.RED).get(i);
				tile.rotate(i - (main.side == Side.FRONT ? 2 : 3));
				tile.flip();
				add(tile, corePos[i]);
			}

			for (int i = 0; i < 3; i++) {
				TerminalTile tile = ts.getTerminalTiles(Color.GREEN).get(i);
				tile.rotate(i + (main.side == Side.FRONT ? 1 : 0));
				tile.flip();
				add(tile, corePos[i + 3]);
			}
		}
	}

	public boolean add(Tile tile, Point cell) {
		if (tile == null)
			return false;
		tile.setCell(cell);
		placedTiles[cell.x][cell.y] = tile;
		super.add(tile);
		if (!coreReady() && tilesOnTable > 0)
			core[tilesOnTable - 1] = (TerminalTile) tile;
		tilesOnTable++;
		if (tilesOnTable == 7)
			main.getTileMarket().clear();
		main.setRefresh(0);
		refresh.add(true);
		refresh.add(false);
		return true;
	}

	public boolean add(Point cell) {
		if (main.getActiveTile() == null)
			return false;
		Tile activeTile;
		try {
			activeTile = main.getActiveTile().clone();

			activeTile.setCell(cell);

			if (isOccupied(cell) || isLonely(cell) || coreViolation(activeTile) || circuitBreaker(activeTile)) {
				activeTile = null;
				return false;
			}
			if (add(activeTile, cell)) {
				main.setActiveTile(null);
				if (coreReady()) {
					WireChaser.dischargeTerminals(this, activeTile);
					main.getPlayerController().discharge();
				}
				// System.out.println("Tile" + activeTile.getTileNumber() + "
				// placed");
				main.setMessage("Tile" + activeTile.getTileNumber() + " placed");
				if (!coreReady() && tilesOnTable > 0)
					core[tilesOnTable - 1] = (TerminalTile) activeTile;
				return true;
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void render(Graphics2D g2d) {
		g2d.drawImage(main.getTexture().getTilesDump(),0,0,null);
		for (int i = 0; i < size(); i++)
			if (refresh.get(i))
				get(i).renderShadow(g2d);
		for (int i = 0; i < size(); i++)
			if (refresh.get(i))
				get(i).render(g2d);
	}

	public boolean tick() {
		boolean ans = false;
		for (int i = 0; i < size(); i++) {
			boolean upd = get(i).tick();
			if (!upd&refresh.get(i))
				get(i).render((Graphics2D) main.getTexture().getTilesDump().getGraphics());
			ans |= upd;
			refresh.set(i, upd);
		}
		return ans;
	}

	public Tile[] getTiles(Point[] cells) {
		Tile[] nTiles = new Tile[cells.length];
		for (int i = 0; i < cells.length; i++)
			if (cells[i].x >= 0 && cells[i].y >= 0)
				nTiles[i] = placedTiles[cells[i].x][cells[i].y];
		return nTiles;
	}

	public Main getMain() {
		return main;
	}

	public Tile getFacingTile(Tile t, int side) {
		Point p = main.getGrid().neightbours(t.getCell())[side];
		try{
			Tile outTile = placedTiles[p.x][p.y];
			return outTile;
		}
		catch(Exception e){
			return null;
		}
		// if (outTile==null){
		// System.out.println("["+p.x+", "+p.y+"]");
		// occupiedCells();}
	}

	public Interface getFacingInterface(Tile t, int side) {
		Tile checkTile = getFacingTile(t, side);
		if (checkTile == null)
			return null;
		return checkTile.getInterface(Tile.opposite(side));
	}

	public Rectangle getCoreClickArea() {
		return get(0).getClickArea();
	}

	public boolean isTerminal(Tile t) {
		for (TerminalTile tTile : core)
			if (t.getTileNumber() == tTile.getTileNumber())
				return true;
		return false;
	}

	public boolean coreReady() {
		return tilesOnTable > 6;
	}

	/*
	 * private void occupiedCells() { for (int i = 0; i < 64; i++) for (int j =
	 * 0; j < 64; j++) if (placedTiles[i][j] != null) System.out.print("[" + i +
	 * ", " + j + "] "); System.out.println(); }
	 */

	public void calcDelays(PlayerController pc) {
		for (TerminalTile tTile : core) {
			int delay = WireChaser.calcDelay(main.getTc(), tTile);
			Player p = pc.getPlayerByColor(tTile.getColor());
			p.setCircDelay(tTile.getSpeed(), delay);
		}
	}

	// ------------RULES CHECK
	public boolean isOccupied(Point cell) {
		if (placedTiles[cell.x][cell.y] != null) {
			// System.out.println("Occupied");
			main.setMessage("Place tile in a free space");
			return true;
		}
		return false;
	}

	private boolean isLonely(Point cell) {
		Tile[] tiles = getTiles(main.getGrid().neightbours(cell));
		for (Tile t : tiles) {
			if (t != null)
				return false;
		}
		// System.out.println("Lonely");
		main.setMessage("Place tile next to an existing one");
		return true;
	}

	private boolean coreViolation(Tile t) {
		if (coreReady())
			return false;
		Point[] coord = main.getGrid().neightbours(main.getGrid().getCenter());
		Point pos = t.getCell();
		int side = -1;
		for (int i = 0; i < 6; i++)
			if (pos.getX() == coord[i].getX() && pos.getY() == coord[i].getY()) {
				side = i;
				break;
			}
		if (side < 0) {
			main.setMessage("Place tile next to core");
			return true;
		}
		int coreSide = t.whereIsThis(new Interface(IO.OUTPUT, Device.CORE)).get(0);
		t.rotate((side > coreSide ? -3 - coreSide + side : 3 - coreSide + side));
		return false;
	}

	private boolean circuitBreaker(Tile t) {
		Player p = main.getPlayerController().getActivePlayer();
		ArrayList<TerminalTile> terminals = new ArrayList<TerminalTile>();
		if (p.getChargeTokens() == 0)
			return false;
		for (int i = 0; i < 6; i++) {
			Interface interf = t.getInterface(i);
			if (interf.getIo() == IO.INPUT || interf.getDevice() == Device.CONST0
					|| interf.getDevice() == Device.CONST1) {
				Tile checkTile = getFacingTile(t, i);
				if (checkTile != null && getFacingInterface(t, i).getIo() != IO.OUTPUT) {
					terminals.addAll(WireChaser.chaseInOut(this, checkTile, Tile.opposite(i)));
				}
			}
		}
		for (TerminalTile tTile : terminals)
			if (tTile.getColor() != p.getColor()) {
				main.setMessage("You cannot block other's circuits untill you're discharged");
				return true;
			}
		return false;
	}

	// SCORE COUNTING
	public void addSparks(Tile t, SparkController sc) {
		ArrayList<Integer> inputs = t.listSides(IO.INPUT);
		for (Integer i : inputs) {
			if (getFacingTile(t, i) == null)
				sc.add(t, i, false);
			else {
				Interface interf = getFacingInterface(t, i);
				if (interf.getIo() == IO.INPUT)
					sc.add(t, i, false);
				if (interf.getDevice() == Device.CONST0)
					sc.add(t, i, false);
				if (interf.getDevice() == Device.CONST1)
					sc.add(t, i, true);
			}
		}
	}

	public void addSparks(SparkController sc) {
		for (Tile t : this)
			addSparks(t, sc);
	}

	public TerminalTile[] getCore() {
		return core;
	}

	public int getTilesOnTable() {
		return tilesOnTable;
	}
}
