package player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import main.GameState;
import main.Main;
import tiles.ProjectTile0;
import tiles.ProjectTile1;

public class PlayerController extends ArrayList<Player> {
	private ArrayList<Player> chooseOrder;
	private ArrayList<Player> placementOrder;
	private ArrayList<Player> lastPlacementOrder;
	private ArrayList<Player> lastChooseOrder;
	private ArrayList<Player> hudsOrder;
	private ArrayList<Color> colorList;
	private Player activePlayer;
	private Main main;
	private ArrayList<HUD> huds;
	private int skips = 0;
	private boolean refresh = true;
	private BufferedImage hCopy;

	// CONSTRUCTORS
	public PlayerController(Main main) {
		this.main = main;
		chooseOrder = new ArrayList<Player>();
		hudsOrder = new ArrayList<Player>();
		huds = new ArrayList<HUD>();
		colorList = new ArrayList<Color>();
		activePlayer = null;
		hCopy = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB);
	}
	// GETTERS&SETTERS

	public void setPowerSolder(Player p) {
		for (Player player : this) {
			player.setPowerSolder(player == p);
		}
	}

	public Rectangle getPowerSolderClickArea() {
		return huds.get(indexOf(activePlayer)).getPowerSolderClickArea();
	}

	// return project square click area
	public Rectangle getActivePlayerClickArea() {
		if (activePlayer != null)
			return huds.get(indexOf(activePlayer)).getClickArea();
		return new Rectangle();
	}

	public int getNumberOfPlayer() {
		return size();
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Player getPlayerByColor(Color color) {
		for (Player p : this)
			if (p.getColor() == color)
				return p;
		return null;
	}

	// return fist player with no charges
	public boolean getFirstDischarged(boolean begin) {
		if (!anyDischarged())
			return false;
		if (begin)
			placementOrder = (ArrayList<Player>) lastPlacementOrder.clone();
		for (int i = 0; i < size(); i++)
			if (nextSwitchPlayer()) {
				if (activePlayer.getChargeTokens() == 0) {
					refresh = true;
					return true;
				}
			} else
				return false;
		return false;
	}

	// METHODS

	// drop pending charges for all players
	public void discharge() {
		for (Player p : this)
			p.discharge();
		refresh = true;
	}

	// prepare color list
	public void addColor(Color color) {
		colorList.add(color);
		main.setMessage("Player " + Integer.toString(colorList.size() + 1) + ", select your color");
		if (colorList.size() == 2)
			main.addMessage(" or click on table to start a 2P match");
	}

	// initialize
	public void createPlayerList(Main main, boolean def) {
		/*
		 * Integer[] options = { 2, 3 }; ArrayList<Color> colors = new
		 * ArrayList<Color>(); colors.add(Color.RED); colors.add(Color.GREEN);
		 * colors.add(Color.BLUE); int nPlayers = 0; while (nPlayers < 2)
		 * nPlayers = 2 + JOptionPane.showOptionDialog(null, "How many players?"
		 * , "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		 * options, 2); for (int i = 1; i <= nPlayers; i++) { String[]
		 * colorArray = new String[colors.size()]; for (int j = 0; j <
		 * colors.size(); j++) colorArray[j] = (colors.get(j) == Color.RED ?
		 * "RED" : (colors.get(j) == Color.GREEN ? "GREEN" : "BLUE")); Color
		 * color = colors.get(Math.max(0, JOptionPane.showOptionDialog(null,
		 * "Player " + i + ", select color", "", JOptionPane.YES_NO_OPTION,
		 * JOptionPane.QUESTION_MESSAGE, null, colorArray, colors.get(0))));
		 * players.add(new Player(color, (nPlayers == 2 ? 18 : 12), main));
		 * colors.remove(color); }
		 */
		if (def) {
			add(new Player(Color.RED, 18, main));
			add(new Player(Color.GREEN, 18, main));
			// players.add(new Player(Color.BLUE, 12, main));
		} else
			for (int i = 0; i < colorList.size(); i++) {
				Color color = colorList.get(i);
				add(new Player(color, (colorList.size() == 2 ? 18 : 12), main));
			}
		for (Player p : this)
			huds.add(new HUD(p, main));
		assignProject(main.getHelper().isActive());
		setPowerSolder(get(0));
		nextPlayerChoose();
	}

	// ORDER MANAGEMENT
	public Player nextPlayerChoose() {
		if (main.getTc().coreReady())
			main.getTc().calcDelays(this);
		if (chooseOrder.size() == 0)
			makeOrder();
		activePlayer = chooseOrder.get(0);
		chooseOrder.remove(activePlayer);
		hudsOrder = lastChooseOrder;
		refresh = true;
		return activePlayer;
	}

	public Player nextPlayerPlace() {
		if (endOfTurn())
			return null;
		if (main.getTc().coreReady())
			main.getTc().calcDelays(this);
		activePlayer = placementOrder.get(0);
		placementOrder.remove(activePlayer);
		skips--;
		refresh = true;
		main.setActiveTile();
		main.setMessage("Place tile");
		main.setState(GameState.PLACE);
		return activePlayer;
	}

	public boolean nextSwitchPlayer() {
		if (placementOrder.size() == 0) {
			if (anySwitchOn()) {
				for (Player p : this)
					if (!p.getSwitch().isOn()) {
						activePlayer = p;
						refresh = true;
						return true;
					}
			}
			return false;
		}
		activePlayer = placementOrder.get(0);
		placementOrder.remove(activePlayer);
		refresh = true;
		return true;
	}

	public boolean skip() {
		if (skips <= 0)
			return false;
		placementOrder.add(activePlayer);
		lastPlacementOrder = (ArrayList<Player>) placementOrder.clone();
		activePlayer.setTile(main.getActiveTile());
		nextPlayerPlace();
		setPowerSolder(activePlayer);
		refresh = true;
		return true;
	}

	public boolean endOfTurn() {
		return placementOrder.size() == 0;
	}

	// order players
	private void makeOrder() {
		chooseOrder = (ArrayList<Player>) super.clone();
		Collections.sort(chooseOrder); // choose order is delay based
		placementOrder = (ArrayList<Player>) super.clone();
		if (main.getTc().coreReady()) { // rotate placement order to put PS on
										// top
			for (Player p : this) {
				if (p.hasPowerSolder())
					break;
				placementOrder.add(p);
				placementOrder.remove(0);
				p.getSwitch().setOn(false);
			}
		} else
			Collections.sort(placementOrder);
		lastChooseOrder = (ArrayList<Player>) chooseOrder.clone();
		lastPlacementOrder = (ArrayList<Player>) placementOrder.clone();
		hudsOrder = lastPlacementOrder;
		skips = size();
	}

	private void assignProject(boolean def) {
		if (def) {// PROJECTS for tutorial
			for (Player p : this) {
				if (p.getColor() == Color.RED)
					p.setProject(new ProjectTile0(3, main), new ProjectTile1(1, main));
				else if (p.getColor() == Color.GREEN)
					p.setProject(new ProjectTile0(4, main), new ProjectTile1(2, main));
			}
			return;
		}
		Random r = new Random();
		ArrayList<ProjectTile0> p0 = new ArrayList<ProjectTile0>();
		ArrayList<ProjectTile1> p1 = new ArrayList<ProjectTile1>();
		ArrayList<Integer> rndArray = new ArrayList<Integer>();
		while (rndArray.size() < size()) {
			int rnd = r.nextInt(3);
			if (!rndArray.contains(rnd)) {
				rndArray.add(rnd);
				p0.add(new ProjectTile0(rnd + 3, main));
			}
		}
		rndArray = new ArrayList<Integer>();
		while (rndArray.size() < size()) {
			int rnd = r.nextInt(3);
			if (!rndArray.contains(rnd)) {
				rndArray.add(rnd);
				p1.add(new ProjectTile1(rnd, main));
			}
		}

		for (int i = 0; i < size(); i++)
			get(i).setProject(p0.get(i), p1.get(i));
	}

	public void calcWinner() {
		for (Player p : this)
			p.setScore(p.getChargeTokens() * 2 + (p.hasPowerSolder() ? 1 : 0));
		makeOrder();
		hudsOrder = chooseOrder;
		activePlayer = (chooseOrder.get(0));
		Color color = activePlayer.getColor();
		main.setMessage(
				(color == Color.RED ? "Red" : (color == Color.GREEN ? "Green" : "Blue")) + " player is the winner!");
		refresh = true;
	}

	// Charge and switches checking
	public boolean anyDischarged() {
		for (Player p : this)
			if (p.getChargeTokens() == 0)
				return true;
		return false;

	}

	public boolean anySwitchOn() {
		for (Player p : this)
			if (p.getSwitch().isOn())
				return true;
		return false;
	}

	public boolean anySwitchOff() {
		for (Player p : this)
			if (!p.getSwitch().isOn())
				return true;
		return false;
	}

	public void setSwitches(boolean on) {
		for (Player p : this)
			p.getSwitch().setOn(on);
	}

	public void penalty() {
		if (activePlayer.hasPowerSolder()) {
			activePlayer.setPowerSolder(false);
			lastPlacementOrder.get(((lastPlacementOrder.indexOf(activePlayer) + 1) % size())).setPowerSolder(true);
		} else
			activePlayer.addCharge(1);
		setSwitches(false);
		refresh = true;
	}

	public void setRefresh() {
		refresh = true;
	}

	// distribute remaining charge after switch on circuit.
	public void chargeDistrib() {
		int chargePot = 0;
		if (anyDischarged()) {
			int dischargedPlayers = 0;
			for (Player p : this) {
				chargePot += p.getChargeTokens();
				if (p.getChargeTokens() == 0)
					dischargedPlayers++;
			}
			chargePot /= 3;
			chargePot /= dischargedPlayers;
		}
		for (Player p : this)
			if (p.getChargeTokens() == 0)
				p.setChargeTokens(chargePot);
			else
				p.setChargeTokens(0);
		activePlayer = null;
		refresh = true;
	}

	public void render(Graphics2D g2d) {
		if (refresh) {
			refresh = false;
			hCopy = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) hCopy.getGraphics();
			for (int i = 0; i < hudsOrder.size(); i++) {
				Player p = hudsOrder.get(i);
				HUD h = huds.get(indexOf(p));
				h.setSize(p == activePlayer ? 1.4 : 1);
				h.setYPos(i * 65 + 30);
				h.render(g);
			}
			for (Player p : this) {
				Switch s = p.getSwitch();
				s.setxPos(indexOf(p) * 50 + 30);
				s.setyPos(size() * 65 + 30);
				s.render(g);
			}
		}
		g2d.drawImage(hCopy, 0, 0, null);
	}

}
