package utility;

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

	public StateController(Main main) {
		this.main = main;
		tc = main.getTc();
		pc = main.getPlayerController();
	}

	public void nextState(int x, int y) {
		Point pFrame = new Point(x, y);
		if (main.getState()!=GameState.MENU&&main.getHelper().isActive())
			if (main.getHelper().isClicked(pFrame)||main.getHelper().isPaused())
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
				main.debug = false;
				main.resetControllers();
				main.getHelper().setActive(false);
				main.setState(GameState.SELECT_SIDE);
				main.sideMarket();
				break;
			case 1:
				main.debug = true;
				main.resetControllers();
				main.getHelper().setActive(false);
				main.setState(GameState.SELECT_SIDE);
				main.sideMarket();
				break;
			case 2:
				openMenu();
				break;
			case 3:
				main.debug = false;
				main.resetControllers();
				main.getTileStack().makeStack(true);
				main.getHelper().setActive(true);
				main.setState(GameState.SELECT_SIDE);
				main.sideMarket();
				break;
			case 4:
				main.stop();
			}

		case SELECT_SIDE: {
			Tile t = tileMarket.select(x, y);

			if (t != null) {
				main.side = t.getSide();
				main.getTileStack().setSide(main.side);
				main.getTermStock().setSide(main.side);
				main.colorMarket();
				if (main.debug) {
					main.startMatch();
					main.setState(GameState.DRAW);
				} else
					main.setState(GameState.SELECT_PLAYERS);
			}
			break;
		}

		case SELECT_PLAYERS: {
			TerminalTile t = (TerminalTile) tileMarket.select(x, y);
			if (t != null) {
				pc.addColor(t.getColor());
				tileMarket.removeByColor(t.getColor());
				if (tileMarket.isEmpty()) {
					main.startMatch();
					main.setState(GameState.DRAW);
				}
				break;
			}
			if (tileMarket.size() < 4) {
				main.startMatch();
				main.setState(GameState.DRAW);
			}
			break;
		}

		case DRAW:
			if (main.getTileStack().size() < pc.getNumberOfPlayer()) {
				goToScore();
				pc.setSwitches(true);
			} else if (clickOnCore(p)) {
				main.setTileMarket();
				main.setState(GameState.MARKET);
				main.setMessage("Select a tile");
			}
			break;

		case PLACE:
			if (tc.coreReady() && pc.getPowerSolderClickArea().contains(pFrame)) {
				if (pc.skip()) {
					main.setMessage("Power solder yielded");
					main.setActiveTile();
					main.setState(GameState.PLACE);
					break;
				} else {
					main.setMessage("Cannot skip");
					break;
				}
			}
			if (!main.getTileStack().getStackPos().contains(p))
				if (tc.addTile(main.getGrid().nearest(p))) {
					if (!tc.coreReady()) {
						pc.nextPlayerChoose();
						main.addMessage(", draw a terminal tile (click on pile or core)");
						main.setState(GameState.DRAW);
					} else // if (pc.getActivePlayer().getChargeTokens() != 0 &&
							// !pc.anySwitchOn()) {
						nextPlayer();
				} /*
					 * else { main.setMessage(
					 * "Do you want to turn circuit on? (click on pile or core to pass)"
					 * ); main.setState(GameState.SWITCH_SELECT); }
					 * 
					 * }
					 */
			break;

		case MARKET:
			if (main.getPlayerController().getActivePlayer().setTile(tileMarket.select(x, y)))
				if (!tc.coreReady()) {
					pc.nextPlayerPlace();
					main.setActiveTile();
					main.setState(GameState.PLACE);
					main.setMessage("Place tile");
				} else if (tileMarket.isEmpty()) {
					pc.nextPlayerPlace();
					main.setActiveTile();
					main.setState(GameState.PLACE);
					main.setMessage("Place tile");
				} else {
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
				if (!pc.anySwitchOff()) {
					goToScore();
				}
			} else if (clickOnCore(p)) {
				if (pc.anySwitchOn()) {
					pc.penalty();
					pc.setSwitches(false);
					pc.nextPlayerChoose();
					main.setMessage("Draw new tiles (click on pile or core)");
					main.getHelper().setDischarged(false);
					main.setState(GameState.DRAW);
				} else {
					if (pc.getFirstUncharged(false)) {
						main.setMessage("Do you want to turn circuit on? (click on pile or core to pass)");
						main.setState(GameState.SWITCH_SELECT);
					} else {
						pc.nextPlayerChoose();
						main.setMessage("Draw new tiles (click on pile or core)");
						main.getHelper().setDischarged(false);
						main.setState(GameState.DRAW);
					}
				}
			}
			break;
		case SCORE:
			SparkController sc = main.getSparkController();
			sc.moveSparks();
			if (sc.allProcessed()) {
				main.setState(GameState.ENDGAME);
				pc.setWinner();
			} else
				main.setMessage("Keep the beat and rock around the clock!");
			break;
		case ENDGAME:
		default:
			break;
		}
		main.getHelper().pauseConditions();
		main.setRefresh();
	}

	private void nextPlayer() {
		if (pc.endOfTurn()) {
			if (pc.getFirstUncharged(true)) {
				main.getHelper().setDischarged(true);
				main.getHelper().last();
				main.setMessage("Do you want to turn circuit on? (click on pile or core to pass)");
				main.setState(GameState.SWITCH_SELECT);
			} else {
				pc.nextPlayerChoose();
				main.addMessage(", draw new tiles (click on pile or core)");
				main.setState(GameState.DRAW);
			}
		} else {
			pc.nextPlayerPlace();
			main.setActiveTile();
			main.setMessage("Place tile");
			main.setState(GameState.PLACE);
		}
	}

	private void goToScore() {
		main.setState(GameState.SCORE);
		pc.chargeDistrib();
		main.setMessage("Let's turn on circuit! Beat the clock!");
		tc.addSparks(main.getSparkController());
		main.getSparkController().putTargets();
	}

	private boolean clickOnCore(Point p) {
		return main.getTileStack().getStackPos().contains(p) || tc.getCoreClickArea().contains(p);
	}

	public void openMenu() {
		if (main.getState() == GameState.MENU)
			main.setState(prevState);
		else {
			prevState = main.getState();
			main.setState(GameState.MENU);
		}

	}
}