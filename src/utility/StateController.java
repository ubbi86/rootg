package utility;

import java.awt.Color;
import java.awt.Point;

import main.GameState;
import main.Main;
import player.PlayerController;
import player.Switch;
import tiles.TerminalTile;
import tiles.Tile;
import tiles.TileController;
import tiles.TileMarket;
import tiles.TileStack;

public class StateController {
	private Main main;
	private GameState prevState = GameState.MENU;
	TileController tc;
	PlayerController pc;
	TileStack ts;

	// CONSTRUCTORS
	public StateController(Main main) {
		this.main = main;
		tc = main.getTc();
		pc = main.getPlayerController();
		ts = main.getTileStack();
	}

	// METHODS
	public void nextState(int x, int y) {
		Point pFrame = new Point(x, y);
		if (main.getState() != GameState.MENU && main.getHelper().isActive())
			if (main.getHelper().isClicked(pFrame) || main.getHelper().isPaused())
				main.getHelper().nextStep();
			else
				return;
		Point p = new Point((int) ((x - main.WIDTH / 2) / main.getZoom() - main.getxCam()),
				(int) ((y - main.HEIGHT / 2) / main.getZoom() - main.getyCam()));
		TileMarket tileMarket = main.getTileMarket();
		if (main.getState() != GameState.PROJECT && tc.coreReady() && pc.getActivePlayerClickArea().contains(pFrame)) {
			prevState = main.getState();
			main.setState(GameState.PROJECT);
			main.setMessage("Ask your opponents to look away and memorize your project (or scan the QR!)");
			return;
		}
		switch (main.getState()) {
		case MENU:
			switch (main.getMenu().select(pFrame)) {
			case 0:
				selectMenu(false, false);
				break;
			case 1:
				selectMenu(true, false);
				break;
			case 2:
				menu();
				break;
			case 3:
				selectMenu(false, true);
				break;
			case 4:
				main.stop();
			}

		case SELECT_SIDE: {
			Tile t = tileMarket.select(x, y);
			if (t != null) {
				main.setSide(t.getSide());
				main.colorMarket();
				if (main.debug)
					main.startMatch();
				else
					main.setState(GameState.SELECT_PLAYERS);
			}
			break;
		}

		case SELECT_PLAYERS: {
			TerminalTile t = (TerminalTile) tileMarket.select(x, y);
			if (t != null) {
				Color color = t.getColor();
				pc.addColor(color);
				tileMarket.removeByColor(color);
				if (tileMarket.isEmpty())
					main.startMatch();
				break;
			}
			if (tileMarket.size() < 4)
				main.startMatch();
			break;
		}

		case DRAW:
			if (ts.size() < pc.getNumberOfPlayer()) {
				goToScore();
				pc.setSwitches(true);
			} else if (clickOnCore(p)) {
				main.setTileMarket();
			}
			break;

		case PLACE:
			if (tc.coreReady() && pc.getPowerSolderClickArea().contains(pFrame))
				if (pc.skip())
					main.setMessage("Power solder yielded");
				else
					main.setMessage("Cannot skip");
			else if (!ts.getStackPos().contains(p))
				if (tc.add(main.getGrid().nearest(p))) {
					if (!tc.coreReady()) {
						pc.nextPlayerChoose();
						goToDraw();
					} else // if (pc.getActivePlayer().getChargeTokens() != 0 &&
							// !pc.anySwitchOn()) {
						nextPlayer();
				}
			break;

		case MARKET:
			if (main.getPlayerController().getActivePlayer().setTile(tileMarket.select(x, y)))
				if (!tc.coreReady())
					pc.nextPlayerPlace();
				else if (tileMarket.isEmpty())
					pc.nextPlayerPlace();
				else {
					pc.nextPlayerChoose();
					main.setState(GameState.MARKET);
				}
			break;

		case PROJECT:
			Tile t = pc.getActivePlayer().getProjectMarket().select(x, y, false);
			if (t != null) {
				main.setMessage("Double check for staring opponents before flipping the second tile");
				t.flip();
			} else {
				pc.getActivePlayer().hideProject();
				main.setMessage("Back to play. Thank your opponents for their fairplay");
				main.getTileMarket().setRefresh();
				main.setState(prevState);
			}
			break;

		case SWITCH_SELECT:
			Switch s = pc.getActivePlayer().getSwitch();
			if (s.getClickArea().contains(pFrame)) {
				s.setOn(true);
				main.setMessage("Do you want to turn circuit on? (you'll be penalized if you don't... feel free)");
				pc.nextSwitchPlayer();
				if (!pc.anySwitchOff())
					goToScore();
			} else if (clickOnCore(p)) {
				if (pc.anySwitchOn()) {
					pc.penalty();
					pc.nextPlayerChoose();
					goToDraw();
					main.getHelper().setDischarged(false);
				} else {
					if (pc.getFirstDischarged(false)) {
						main.setMessage("Do you want to turn circuit on? (click on pile or core to pass)");
						main.setState(GameState.SWITCH_SELECT);
					} else {
						pc.nextPlayerChoose();
						goToDraw();
						main.getHelper().setDischarged(false);
					}
				}
			}
			break;

		case SCORE:
			SparkController sc = main.getSparkController();
			sc.moveSparks();
			if (sc.allProcessed()) {
				main.setState(GameState.ENDGAME);
				pc.calcWinner();
			} else
				main.setMessage("Keep the beat and rock around the clock!");
			break;
		case ENDGAME:
		default:
			break;
		}
		main.getHelper().pauseConditions();
	}

	public void menu() {
		if (main.getState() == GameState.MENU)
			main.setState(prevState);
		else {
			prevState = main.getState();
			main.setState(GameState.MENU);
		}
	}

	private void nextPlayer() {
		if (pc.endOfTurn()) {
			if (pc.getFirstDischarged(true)) {
				main.getHelper().setDischarged(true);
				main.getHelper().last();
				main.setMessage("Do you want to turn circuit on? (click on pile or core to pass)");
				main.setState(GameState.SWITCH_SELECT);
			} else {
				pc.nextPlayerChoose();
				goToDraw();
			}
		} else {
			pc.nextPlayerPlace();
		}
	}

	private void goToDraw() {
		main.addMessage(", draw new tiles (click on pile or core)");
		main.setState(GameState.DRAW);
	}

	private void goToScore() {
		pc.chargeDistrib();
		main.setMessage("Let's turn on circuit! Beat the clock!");
		tc.addSparks(main.getSparkController());
		main.getSparkController().addTargetSparks();
		main.setState(GameState.SCORE);
	}

	private boolean clickOnCore(Point p) {
		return ts.getStackPos().contains(p) || tc.getCoreClickArea().contains(p);
	}

	private void selectMenu(boolean def, boolean help) {
		main.debug = def;
		main.resetControllers();
		main.getHelper().setActive(help);
		main.setState(GameState.SELECT_SIDE);
		main.sideMarket();
	}

}