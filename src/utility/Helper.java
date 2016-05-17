package utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import main.HexGrid;
import main.Main;
import player.Player;
import player.PlayerController;
import tiles.Tile;
import tiles.TileController;
import tiles.TileMarket;
import tiles.TileStack;

public class Helper {
	private HelperCursor cursor;
	private ArrayList<String> texts;
	private ArrayList<Object> positions;
	private ArrayList<Boolean> frameRel;
	private ArrayList<Integer> angles;
	private Main main;
	private boolean active = false;
	private boolean paused = false;
	private String currentText = "";
	private Font font;
	private int angle;
	private boolean discharged;
	private boolean firstDischarged;

	// CONSTRUCTORS
	public Helper(Main main) {
		this.main = main;
		cursor = new HelperCursor(null, main);
		texts = new ArrayList<String>();
		positions = new ArrayList<Object>();
		frameRel = new ArrayList<Boolean>();
		angles = new ArrayList<Integer>();
	}

	// GETTERS&SETTERS
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDischarged(boolean discharged) {
		if (active)
			this.discharged = discharged;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isClicked(Point p) {
		return cursor.isClicked(p) && checkAngle();
	}

	public boolean isPaused() {
		return paused;
	}

	//METHODS
	public boolean nextStep() {
		if (paused) {
			return false;
		}
		if (texts.size() > 0) {
			currentText = texts.get(0);
			if (active) System.out.println(currentText);
			angle = angles.get(0);
			cursor.setFrameRel(frameRel.get(0));
			try {
				cursor.setPos((Tile) positions.get(0));
			} catch (Exception e) {
				cursor.setPos((Point) positions.get(0));
				cursor.setTile(null);
			}
			texts.remove(0);
			positions.remove(0);
			frameRel.remove(0);
			angles.remove(0);
			return false;
		}
		return true;
	}

	public void pauseConditions() {
		if (!active)
			return;
		TileController tc = main.getTc();
		PlayerController pc = main.getPlayerController();
		int tilesOnTable = tc.getTilesOnTable();
		boolean testPause = (tilesOnTable > 3 && !tc.coreReady()) || (tilesOnTable > 10 && !(discharged));// other
																											// conditions
		if (paused && !testPause) {
			paused = testPause;
			nextStep();
			return;
		}
		paused = testPause;

	}

	public void userTest(StateController sc) {
		if (active && !paused) {
			Point p = new Point((int) cursor.getPos().getX(), (int) cursor.getPos().getY());
			if (!cursor.frameRel)
				p = new Point((int) ((p.x + main.getxCam()) * main.getZoom() + main.WIDTH / 2),
						(int) ((p.y + main.getyCam()) * main.getZoom() + main.HEIGHT / 2));
			if (main.getActiveTile() != null && angle >= 0)
				main.getActiveTile().setAngle(angle);
			sc.nextState(p.x, p.y);
		}
	}

	private void add(String text, int x, int y) {
		add(text, x, y, true);
	}

	private void add(String text, Point p) {
		add(text, p.x, p.y, true, -1);
	}

	private void add(String text, Point p, boolean frameRel) {
		add(text, p.x, p.y, frameRel, -1);
	}

	private void add(String text, Point p, boolean frameRel, int angle) {
		add(text, p.x, p.y, frameRel, angle);
	}

	private void add(String text, int x, int y, boolean frameRel) {
		add(text, x, y, frameRel, -1);
	}

	private void add(String text, int x, int y, boolean frameRel, int angle) {
		texts.add(text);
		positions.add(new Point(x, y));
		this.frameRel.add(frameRel);
		this.angles.add(angle);
	}

	private void add(String text, Tile t) {
		add(text, t, false, -1);
	}

	private void add(String text, Tile t, boolean frameRel) {
		add(text, t, frameRel, -1);
	}

	private void add(String text, Tile t, boolean frameRel, int angle) {
		texts.add(text);
		positions.add(t);
		this.frameRel.add(frameRel);
		this.angles.add(angle);
	}

	public boolean checkAngle() {
		if (angle < 0 || main.getActiveTile() == null)
			return true;
		return main.getActiveTile().getAngle() == angle;
	}

	public void tick() {
		if (!active)
			return;
		cursor.tick();
	}

	public void render(Graphics2D g2d) {
		if (!active)
			return;
		if (!paused)
			cursor.render(g2d);
		g2d.setFont(font);
		g2d.setColor(Color.RED);
		g2d.drawString(currentText, 50, main.getHeight() - 50);
	}

	// LISTS
	public void init() {
		int h = main.HEIGHT;
		int w = main.WIDTH;
		discharged = false;
		firstDischarged = false;
		font = main.getFont().deriveFont(22f);
		HexGrid grid = main.getGrid();
		TileStack stack = main.getTileStack();
		TileMarket tileMarket = main.getTileMarket();
		Tile core = main.coreB;
		add("Welcome to the ROOT\\G: tutorial. This spark will be your guide. Click on it!", w - 300, h - 75);
		add("First you need to choose the side you want to play with. Choose abstract side, it's easier", core, true);
		add("Then you'll choose the color. Freely choose the red one",
				main.getTermStock().getTerminalTile(Color.RED, 1), true);
		add("Your opponent will choose another color. Green for example",
				main.getTermStock().getTerminalTile(Color.GREEN, 1), true);
		add("We will play a two players match, so click on table", w / 3, h / 3);
		add("Get familiar with controls. Explore the table with the middle click or arrow keys to find the spark",
				w + 50, 500, false);
		add("You can do the same with right click or + and - to zoom in and out", w - 50, 2 * h, false);
		add("Here you can see whose turn is: the number in the big square shows the charge of the player.", 150, 50);
		add("This is the core. You can click on it if you need to draw tiles", core);
		add("Each player has its own TERMINAL TILES, each one with different speed", w / 2, h / 3);
		add("Choose the fast one", main.getTermStock().getTerminalTile(Color.RED, 3), true);
		add("Then place the tile next to core", grid.getCenterNeightboursCoord(0), false);
		add("Player have to place their terminal tiles in turn. Second player, please draw.", core);
		add("Choose the slow one", main.getTermStock().getTerminalTile(Color.GREEN, 1), true);
		add("Then place the tile next to core, wherever you want. You want here.", grid.getCenterNeightboursCoord(2),
				false);
		add("You can also draw new tiles clicking on the stack", main.getTileStack().getCenter(), false);
		add("Choose the normal one", main.getTermStock().getTerminalTile(Color.RED, 2), true);
		add("Place it here", grid.getCenterNeightboursCoord(4), false);
		add("Go on and complete the core. I'll be back once you've finished", 0, 0);
		add("Core is complete. Fastest player is the first to choose the new tile.", core);
		add("These are the circuit tiles. Some of them contain logic gates, like this one.", tileMarket.getPosition(0),
				true);
		add("The slower player will choose the remaining one. This one has only wires in it.",
				tileMarket.getPosition(1), true);
		add("You can rotate the tile using the mouse wheel or SPACEBAR and CTRL before placing it.",
				grid.neightbour(grid.neightbourCell(grid.neightbourCell(grid.getCenter(), 3), 3), 3), false);
		add("Each new tile has to be placed near one of the previous tiles.",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 3), 3), false);
		add("Rotate tile to connect this logic gate to the core.",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 4), 4), false, 120);
		add("Whenever you connect a logic gate to a terminal the owner of that terminal gets rid of some charge", 150,
				100, true);
		add("In this case, because it is a NORMAL terminal, two charges are disposed.", 150, 100);
		add("A SLOW terminal will drop 1 charge, a FAST terminal will drop 3 charges.", 150, 100);
		add("Dropping charges is good, but attaching gates to terminal slow them down", 25, 150);
		add("You can keep track of the speed of your terminals reading the numbers in the small squares", 25, 150);
		add("Each logic gate in series will delay your terminal. Understood? You will with practice", 25, 150);
		add("If you followed my advice, now red player should be the fastest. Draw tiles.", core);
		add("BUFFERs are the simplest logic gates. The input value...", tileMarket.getPosition(0).x,
				tileMarket.getPosition(0).y + 100, true);
		add("...is coming out at the output, unchanged.", tileMarket.getPosition(0).x,
				tileMarket.getPosition(0).y - 100, true);
		add("So INPUT 0, OUTPUT 0", tileMarket.getPosition(0), true);
		add("INPUT 1, OUTPUT 1", tileMarket.getPosition(1), true);
		add("This super cool tile is called the POWER SOLDERER: who has it will place its tile first", 250, 50);
		add("When you have the POWER SOLDERER you can always yield it to the next player...", 180, 50);
		add("...and let him place first. But he won't be able to do the same!", 180, 100);
		add("You can also connect numbers to the circuits...",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 2), 2), false, 0);
		add("...but you won't be able to do it on the circuits of your opponent...",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 2), 2), false, 0);
		add("(at least until you've dropped your last charge)",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 2), 2), false, 0);
		add("Rotate tile and place it with buffer pointing away from the core.",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 2), 2), false, 120);
		add("When you try to connect gates in the wrong direction it's like connecting a 0...",
				grid.neightbour(grid.neightbourCell(grid.neightbourCell(grid.getCenter(), 2), 2), 2), false);
		add("...and you can't do that on your opponent's circuits.",
				grid.neightbour(grid.neightbourCell(grid.neightbourCell(grid.getCenter(), 2), 2), 2), false);
		add("(at least until you've dropped your last charge)",
				grid.neightbour(grid.neightbourCell(grid.neightbourCell(grid.getCenter(), 2), 2), 2), false);
		add("Now rotate the tile and place it in a polite way. Please.",
				grid.neightbour(grid.neightbourCell(grid.getCenter(), 2), 2), false, 300);
		add("Whenever you are tired, you can always right click and take a break in the main menu", core);
		add("Probably you're wondering: why should I do all that?", core);
		add("I'll tell you a secret: each player has a secret objective...", core);
		add("And, during your turn, you can discover yours clicking on the big square", 75, 50);
		add("If you flip these tiles...", tileMarket.getPosition(0), true);
		add("...you will discover whether you have to get a 0 or 1 to your terminals.", tileMarket.getPosition(1),
				true);
		add("It's a matter of associating colors, letters and numbers: guess...Fast1, Norm1, Slow1",
				tileMarket.getPosition(0), true);
		add("...but if you're lazy just scan QR code with your phone.", w / 2, h / 2, true);
		add("Place your tile here.", grid.neightbour(grid.neightbourCell(grid.getCenter(), 0), 0), false);
		add("Try to place some tiles and discharge completely one player. I'll be back then", 0, 0);
		add("A player is now able to turn on circuit and end the match", grid.getCenterNeightboursCoord(4), false);
		nextStep();
	}

	public void last() {
		if (firstDischarged)
			return;
		firstDischarged = true;
		HexGrid grid = main.getGrid();
		TileStack stack = main.getTileStack();
		TileMarket tileMarket = main.getTileMarket();
		Tile core = main.coreB;
		Point pFinal = new Point(main.WIDTH - 200, main.HEIGHT - 300);
		boolean testC = main.getPlayerController().getPlayerByColor(Color.RED).getChargeTokens() == 0;
		Player pDisc = (testC ? main.getPlayerController().getPlayerByColor(Color.RED)
				: main.getPlayerController().getPlayerByColor(Color.GREEN));
		Player pOther = (testC ? main.getPlayerController().getPlayerByColor(Color.GREEN)
				: main.getPlayerController().getPlayerByColor(Color.RED));
		add("He doesn't have to, but if he wants to he must click on his LED. Just do it.", pDisc.getSwitch().getPos(),
				true);
		add("The other player may refuse. If he does, he will loose the Power Solderer...", pDisc.getSwitch().getPos(),
				true);
		add("...and if he doesn't have it he will get one extra charge.", pDisc.getSwitch().getPos(), true);
		add("To refuse he just needs to click on the core", core);
		add("Add a couple more tiles", 0, 0);
		add("This time we will turn on the circuit.", pDisc.getSwitch().getPos(), true);
		add("The other player accept, but he's giving 1/3 of his remaining charge to the opponent as points",
				pOther.getSwitch().getPos(), true);
		add("Everything's ready! Signals are entering the circuit and moving to the core", pFinal, true);
		add("If a logic gate has collected all its input it can be overcome.", pFinal, true);
		add("On each click signals try to move to the next logic gate...", pFinal, true);
		add("...and signals gain strength!", pFinal, true);
		add("When a signal reaches a terminal it is processed: ", pFinal, true);
		add("If the signal value is correct, according to the secret project, the terminal owner scores the signal strength",
				pFinal, true);
		add("If a signal reaches the core, all other signals will loose strength...", pFinal, true);
		add("...so try to reach the core just before your opponent!", pFinal, true);
		add("When all terminal have processed a signal finally we'll have a winner", pFinal, true);
		add("and in case of a draw, victory will be with for the POWER SOLDERER.", pFinal, true);
		add("That's the end of the track. Thanks for your patience and enjoy ROOT\\G:!", core);
	}

}
