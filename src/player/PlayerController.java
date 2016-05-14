package player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import main.Main;
import tiles.ProjectTile0;
import tiles.ProjectTile1;

public class PlayerController {
	private ArrayList<Player> players;
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

	public PlayerController(Main main) {
		this.main = main;
		players = new ArrayList<Player>();
		chooseOrder = new ArrayList<Player>();
		hudsOrder = new ArrayList<Player>();
		huds = new ArrayList<HUD>();
		colorList = new ArrayList<Color>();
		activePlayer = null;
		hCopy = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB);
	}

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
			players.add(new Player(Color.RED, 18, main));
			players.add(new Player(Color.GREEN, 18, main));
			// players.add(new Player(Color.BLUE, 12, main));
		} else
			for (int i = 0; i < colorList.size(); i++) {
				Color color = colorList.get(i);
				players.add(new Player(color, (colorList.size() == 2 ? 18 : 12), main));
			}
		for (Player p : players)
			huds.add(new HUD(p, main));
		assignProject();
		setPowerSolder(players.get(0));
		nextPlayerChoose();
	}

	private void makeOrder() {
		chooseOrder = (ArrayList<Player>) players.clone();
		Collections.sort(chooseOrder);
		placementOrder = (ArrayList<Player>) players.clone();
		if (main.getTc().coreReady()) {
			for (Player p : players) {
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
		skips = players.size();
	}

	public void nextPlayerChoose() {
		if (main.getTc().coreReady())
			main.getTc().calcDelays(this);
		if (chooseOrder.size() == 0)
			makeOrder();
		activePlayer = chooseOrder.get(0);
		chooseOrder.remove(activePlayer);
		hudsOrder = lastChooseOrder;
		refresh = true;
	}

	public void nextPlayerPlace() {
		if (main.getTc().coreReady())
			main.getTc().calcDelays(this);
		activePlayer = placementOrder.get(0);
		placementOrder.remove(activePlayer);
		skips--;
		refresh = true;
	}

	public boolean endOfTurn() {
		return placementOrder.size() == 0;
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

	public void setPowerSolder(Player p) {
		for (Player player : players) {
			player.setPowerSolder(player == p);
		}
	}

	public Rectangle getPowerSolderClickArea() {
		return huds.get(players.indexOf(activePlayer)).getPowerSolderClickArea();
	}

	public int getNumberOfPlayer() {
		return players.size();
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Player getPlayerByColor(Color color) {
		for (Player p : players)
			if (p.getColor() == color)
				return p;
		return null;
	}

	/*
	 * private void tick() { chooseOrder.get(0).tick(); }
	 */
	public void render(Graphics2D g2d) {
		if (refresh) {
			refresh = false;
			hCopy = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) hCopy.getGraphics();
			for (int i = 0; i < hudsOrder.size(); i++) {
				Player p = hudsOrder.get(i);
				HUD h = huds.get(players.indexOf(p));
				h.setSize(p == activePlayer ? 1.4 : 1);
				h.setYPos(i * 65 + 30);
				h.render(g);
			}
			for (Player p : players) {
				Switch s = p.getSwitch();
				s.setxPos(players.indexOf(p) * 50 + 30);
				s.setyPos(players.size() * 65 + 30);
				s.render(g);
			}

		}
		g2d.drawImage(hCopy, 0, 0, null);
	}

	public void discharge() {
		for (Player p : players)
			p.discharge();
		refresh = true;
	}

	public void addColor(Color color) {
		colorList.add(color);
		main.setMessage("Player " + Integer.toString(colorList.size() + 1) + ", select your color");
		if (colorList.size() == 2)
			main.addMessage(" or click on table to start a 2P match");
	}

	private void assignProject() {
		if(main.getHelper().isActive()){
			for (Player p:players){
				if(p.getColor()==Color.RED)
					p.setProject(new ProjectTile0(3, main), new ProjectTile1(1, main));
				else if(p.getColor()==Color.GREEN)
					p.setProject(new ProjectTile0(4, main), new ProjectTile1(2, main));
			}
			return;
		}
		Random r = new Random();
		ArrayList<ProjectTile0> p0 = new ArrayList<ProjectTile0>();
		ArrayList<ProjectTile1> p1 = new ArrayList<ProjectTile1>();
		ArrayList<Integer> rndArray = new ArrayList<Integer>();
		while (rndArray.size() < players.size()) {
			int rnd = r.nextInt(3);
			if (!rndArray.contains(rnd)) {
				rndArray.add(rnd);
				p0.add(new ProjectTile0(rnd + 3, main));
			}
		}
		rndArray = new ArrayList<Integer>();
		while (rndArray.size() < players.size()) {
			int rnd = r.nextInt(3);
			if (!rndArray.contains(rnd)) {
				rndArray.add(rnd);
				p1.add(new ProjectTile1(rnd, main));
			}
		}

		for (int i = 0; i < players.size(); i++)
			players.get(i).setProject(p0.get(i), p1.get(i));
	}

	public Rectangle getActivePlayerClickArea() {
		if (activePlayer != null)
			return huds.get(players.indexOf(activePlayer)).getClickArea();
		return new Rectangle();
	}

	public boolean anySwitchOn() {
		for (Player p : players)
			if (p.getSwitch().isOn())
				return true;
		return false;
	}

	public boolean anySwitchOff() {
		for (Player p : players)
			if (!p.getSwitch().isOn())
				return true;
		return false;
	}

	public void setSwitches(boolean on) {
		for (Player p : players)
			p.getSwitch().setOn(on);
	}

	public void setRefresh() {
		refresh = true;
	}

	public boolean getFirstUncharged(boolean begin) {
		if (begin)
			placementOrder = (ArrayList<Player>) lastPlacementOrder.clone();
		for (int i = 0; i < players.size(); i++)
			if (nextSwitchPlayer()) {
				if (activePlayer.getChargeTokens() == 0) {
					refresh = true;
					return true;
				}
			} else
				return false;
		return false;
	}

	public boolean nextSwitchPlayer() {
		if (placementOrder.size() == 0) {
			if (anySwitchOn()) {
				for (Player p : players)
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

	public void penalty() {
		if (activePlayer.hasPowerSolder()) {
			activePlayer.setPowerSolder(false);
			lastPlacementOrder.get(((lastPlacementOrder.indexOf(activePlayer) + 1) % players.size()))
					.setPowerSolder(true);
		} else
			activePlayer.addCharge(1);
		refresh = true;
	}

	public void chargeDistrib() {
		int chargePot = 0;
		int dischargedPlayers = 0;
		for (Player p : players) {
			chargePot += p.getChargeTokens();
			if (p.getChargeTokens() == 0)
				dischargedPlayers++;
		}
		chargePot /= 3;
		chargePot /= dischargedPlayers;
		for (Player p : players)
			if (p.getChargeTokens() == 0)
				p.setChargeTokens(chargePot);
			else
				p.setChargeTokens(0);
		activePlayer = null;
		refresh = true;
	}

	public void setWinner(){
		for(Player p:players)
			p.setScore(p.getChargeTokens()*2+(p.hasPowerSolder()?1:0));
		makeOrder();
		hudsOrder=chooseOrder;
		activePlayer=(chooseOrder.get(0));
		Color color=activePlayer.getColor();
		main.setMessage((color==Color.RED?"Red":(color==Color.GREEN?"Green":"Blue"))+" player is the winner!");
		refresh=true;
	}
	
	public boolean anyDischarged(){
		for(Player p:players)
			if (p.getChargeTokens()==0)
				return true;
		return false;

	}
}
