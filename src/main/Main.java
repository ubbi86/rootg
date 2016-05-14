package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;

import player.PlayerController;
import tiles.InterfaceLoader;
import tiles.Side;
import tiles.TerminalTile;
import tiles.TerminalTileStock;
import tiles.Tile;
import tiles.TileController;
import tiles.TileMarket;
import tiles.TileStack;
import utility.Helper;
import utility.KeyboardInput;
import utility.Menu;
import utility.MouseInput;
import utility.SparkController;
import utility.StateController;

public class Main extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 1200;
	public static int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 1;
	public final String TITLE = "ROOT\\G:";
	public static double TILESCALE = 1;
	public Side side = Side.BACK;
	////////////////////////////////////////////// / ///
	public boolean debug = false; // <<========
	////////////////////////////////////////////// \ \\\
	public static final int TILENUMBER = 72;

	final double targetFPS = 40.0;
	private int FPS = 0;
	private Menu menu;

	boolean running = false;
	GameState state = GameState.PLACE;
	private Thread thread;
	private BufferedImage hCopy;
	private boolean refresh = true;

	private StateController stateController;
	private Texture texture;
	private InterfaceLoader intLoader;
	private TileController tc;
	private Tile activeTile;
	private TileStack tileStack;
	private TileMarket mainTileMarket;
	TerminalTileStock termStock;
	private PlayerController pController;
	private SparkController sparkController;

	public Tile coreB;
	public Tile coreF;

	private Helper helper;

	private int xCam = 0;
	private int yCam = 0;
	private double zoom = .5;
	int xCamTemp = xCam;
	int yCamTemp = yCam;
	double zoomTemp = zoom;

	private String message = "";
	static Font font;

	private int numberOfPlayers = 2;

	private HexGrid grid;

	public static void main(String[] args) {
		new Main(false);

	}

	public Main(boolean debug) {
		HEIGHT = (int) (1 * Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		WIDTH = (int) (1 * Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		// TILESCALE=Math.min(HEIGHT/700.,WIDTH/900.);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		JFrame frame = new JFrame(TITLE);
		frame.add(this);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		start();
	}

	public Main() {
		this(false);
	}

	@Override
	public void run() {
		init();
		long lastTime = System.currentTimeMillis();
		double period = 1E3 / targetFPS;
		double delta = 0;
		// int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		lastTime = System.currentTimeMillis();
		while (running) {
			long passed = System.currentTimeMillis() - lastTime;
			delta += passed / period;
			lastTime = System.currentTimeMillis();
			/**/
			if (delta >= 1) {
				while (delta >= 1) {
					tick();
					// updates++;
					delta--;
				}
				render();
				frames++;
			} else
				try {
					Thread.sleep((long) ((period - passed)/2));
				} catch (InterruptedException e) {
				}

			if (System.currentTimeMillis() - timer > 1000) {
				// updates = 0;
				FPS = (int) (frames * 1000 / (System.currentTimeMillis() - timer));
				frames = 0;
				timer = System.currentTimeMillis();
//				helper.userTest(stateController);///<<<<<comment this to disable tutorial test
			}
		}
		stop();

	}

	private void init() {
		grid = new HexGrid((int) (66 * TILESCALE));
		hCopy = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		texture = new Texture();
		intLoader = new InterfaceLoader();
		coreB = new Tile(8, 1, Side.BACK, this);
		coreF = new Tile(8, 1, Side.FRONT, this);

		resetControllers();

		menu = new Menu(this);
		menu.add("Start match");
		menu.add("Start default match");
		menu.add("Continue");
		menu.add("Learn to play");
		menu.add("Quit");

		hCopy = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		hCopy.getGraphics().drawImage(texture.background, 0, 0, WIDTH + 10, HEIGHT + 10, null);
		new KeyboardInput(this);
		new MouseInput(this);
		setState(GameState.MENU);
	}

	public void resetControllers() {
		tc = new TileController(this);
		pController = new PlayerController(this);
		tileStack = new TileStack(this);
		termStock = new TerminalTileStock(this);
		sparkController = new SparkController(this);
		stateController = new StateController(this);
		mainTileMarket = new TileMarket(this);
		helper = new Helper(this);
		setMessage("");
		xCam = -3 / 2 * WIDTH;
		yCam = -2 * HEIGHT;
		InputStream is = Font.class.getResourceAsStream("/cubano.otf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		helper.init();
	}

	public void startMatch() {
		tc.init(debug);
		pController.createPlayerList(this, debug);
		mainTileMarket.clear();
		message = "Click on pile or on core";
		setRefresh();
	}

	private synchronized void start() {
		if (running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;

		running = false;
		System.exit(0);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void tick() {
		refresh |= tc.tick();
		switch (state) {
		case PLACE:
			if (activeTile != null)
				activeTile.tick(false);
			break;
		case MARKET:
			mainTileMarket.tick();
			break;
		case SELECT_PLAYERS:
			mainTileMarket.tick();
			break;
		case PROJECT:
			pController.getActivePlayer().getProjectMarket().tick();
			break;
		case SCORE:
			sparkController.tick();
		case ENDGAME:
			sparkController.tick();
		default:
			break;
		}
		helper.tick();

	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		zoomTemp = zoom;
		xCamTemp = xCam;
		yCamTemp = yCam;
		/////////////////////////////////////
		// g2d.setColor(Color.WHITE);
		// g2d.fillRect(0, 0, WIDTH*2, HEIGHT*2);
		if (refresh) {
			refresh();
		}
		g2d.drawImage(hCopy, 0, 0, WIDTH, HEIGHT, null);
		switch (state) {
		case MENU:
			menu.render(g2d);
			break;
		case MARKET:
			mainTileMarket.render(g2d);
			break;
		case SELECT_SIDE:
			mainTileMarket.render(g2d);
			break;
		case SELECT_PLAYERS:
			mainTileMarket.render(g2d);
			break;
		case PROJECT:
			pController.getActivePlayer().getProjectMarket().render(g2d);
			g2d.drawImage(pController.getActivePlayer().getQrProject(), WIDTH - 300, HEIGHT - 300, null);
			break;
		case SCORE:
			camEngine(true, g2d);
			sparkController.render(g2d);
			camEngine(false, g2d);
			break;
		case ENDGAME:
			camEngine(true, g2d);
			sparkController.render(g2d);
			camEngine(false, g2d);
			break;
		default:
			break;
		}
		renderMessage(g2d);
		renderFPS(g2d);

		if (state == GameState.PLACE) {
			Tile aTile = activeTile;
			if (aTile != null) {
				g2d.scale(zoomTemp, zoomTemp);
				aTile.renderShadow(g2d);
				aTile.render(g2d);
				g2d.scale(1 / zoomTemp, 1 / zoomTemp);
			}
		}
		/////////////////////////////////////
		g2d.dispose();
		pController.render((Graphics2D) bs.getDrawGraphics());
		helper.render((Graphics2D) bs.getDrawGraphics());
		bs.show();
	}

	private void refresh() {
		refresh = false;
		hCopy = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) hCopy.getGraphics();
		g2d.translate(WIDTH / 2, HEIGHT / 2);
		g2d.scale(zoomTemp, zoomTemp);
		renderBkgn(g2d);
		g2d.translate(xCamTemp, yCamTemp);
		tc.render(g2d);
		tileStack.render(g2d);
		camEngine(false, g2d);
	}

	public void setRefresh() {
		this.refresh = true;
	}

	private void renderMessage(Graphics2D g2d) {
		Font sizedFont = font.deriveFont(25f);
		g2d.setFont(sizedFont);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString(message, 200, 50);
	}

	private void renderFPS(Graphics2D g2d) {
		Font sizedFont = font.deriveFont(15f);
		g2d.setFont(sizedFont);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString(FPS + "FPS", WIDTH - 50, HEIGHT - 25);
	}

	private void renderBkgn(Graphics2D g2d) {
		g2d.drawImage(texture.background, xCamTemp + WIDTH, yCamTemp, 2 * WIDTH, 2 * HEIGHT, null);
		g2d.drawImage(texture.background, xCamTemp - WIDTH, yCamTemp, 2 * WIDTH, 2 * HEIGHT, null);
		g2d.drawImage(texture.background, xCamTemp + WIDTH, yCamTemp + 2 * HEIGHT, 2 * WIDTH, 2 * HEIGHT, null);
		g2d.drawImage(texture.background, xCamTemp - WIDTH, yCamTemp + 2 * HEIGHT, 2 * WIDTH, 2 * HEIGHT, null);

	}

	public HexGrid getGrid() {
		return grid;
	}

	public Texture getTexture() {
		return texture;
	}

	public TileController getTc() {
		return tc;
	}

	public Tile getActiveTile() {
		return activeTile;
	}

	public TileStack getTileStack() {
		return tileStack;
	}

	public void setActiveTile(Tile tile) {
		this.activeTile = tile;
	}

	public void setActiveTile() {
		activeTile = pController.getActivePlayer().getTile();
		pController.getActivePlayer().setTile(null);
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public TileMarket getTileMarket() {
		return mainTileMarket;
	}

	public void setTileMarket() {
		if (tc.coreReady()) {
			mainTileMarket.populate();
			return;
		}
		mainTileMarket = getPlayerController().getActivePlayer().getTileMarket();
	}

	public PlayerController getPlayerController() {
		return pController;
	}

	public StateController getStateController() {
		return stateController;
	}

	public SparkController getSparkController() {
		return sparkController;
	}

	public InterfaceLoader getIntLoader() {
		return intLoader;
	}

	public void setMessage(String message) {
		this.message = message;
		setRefresh();
	}

	public Font getFont() {
		return font;
	}

	public void addMessage(String string) {
		message += string;
		setRefresh();

	}

	public int getxCam() {
		return xCam;
	}

	public void addXCam(int xCam) {
		this.xCam += xCam;
		if (this.xCam < -2 * WIDTH)
			this.xCam = -2 * WIDTH;
		if (this.xCam > 0)
			this.xCam = 0;
	}

	public int getyCam() {
		return yCam;
	}

	public void addYCam(int yCam) {
		this.yCam += yCam;
		if (this.yCam < -HEIGHT * 5 / 2)
			this.yCam = -HEIGHT * 5 / 2;
		if (this.yCam > -HEIGHT)
			this.yCam = -HEIGHT;
	}

	public double getZoom() {
		return zoom;
	}

	public void addZoom(double zoom) {
		this.zoom *= zoom;
		if (this.zoom > 2)
			this.zoom = 2;
		if (this.zoom < 0.5)
			this.zoom = 0.5;
	}

	public void sideMarket() {
		mainTileMarket.clear();
		message = "Select the side you want to play with";
		mainTileMarket.add(coreB);
		mainTileMarket.add(coreF);
	}

	public void colorMarket() {
		mainTileMarket.clear();
		message = "Player 1, select your color";

		ArrayList<Tile> terminalTiles = new ArrayList<Tile>();
		for (int speed = 1; speed < 4; speed++)
			for (TerminalTile t : termStock.getTerminalTiles(speed)) {
				t.flip();
				terminalTiles.add(t);
			}
		mainTileMarket.populate(terminalTiles);
	}

	public Menu getMenu() {
		return menu;

	}

	public Helper getHelper() {
		return helper;
	}

	public void camEngine(boolean forward, Graphics2D g2d) {
		if (forward) {
			g2d.translate(WIDTH / 2, HEIGHT / 2);
			g2d.scale(zoomTemp, zoomTemp);
			g2d.translate(xCamTemp, yCamTemp);
		} else {
			g2d.translate(-xCamTemp, -yCamTemp);
			g2d.scale(1 / zoomTemp, 1 / zoomTemp);
			g2d.translate(-WIDTH / 2, -HEIGHT / 2);
		}

	}

	public TerminalTileStock getTermStock() {
		return termStock;
	}
}