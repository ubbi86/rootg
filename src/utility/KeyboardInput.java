package utility;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GameState;
import main.Main;
import tiles.Tile;

public class KeyboardInput implements KeyListener {

	Main main;

	public KeyboardInput(Main main) {
		this.main = main;
		main.addKeyListener(this);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			moveCam(1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			moveCam(-1, 0);
			break;
		case KeyEvent.VK_UP:
			scrollMenu(-1);
			moveCam(0, 1);
			break;
		case KeyEvent.VK_DOWN:
			scrollMenu(1);
			moveCam(0, -1);
			break;
		case KeyEvent.VK_A:
			moveCam(1, 0);
			break;
		case KeyEvent.VK_D:
			moveCam(-1, 0);
			break;
		case KeyEvent.VK_W:
			scrollMenu(-1);
			moveCam(0, 1);
			break;
		case KeyEvent.VK_S:
			scrollMenu(1);
			moveCam(0, -1);
			break;
		case KeyEvent.VK_ADD:
			main.addZoom(1.1);
			break;
		case KeyEvent.VK_SUBTRACT:
			main.addZoom(0.9);
			break;
		case KeyEvent.VK_Q:
			main.addZoom(1.1);
			break;
		case KeyEvent.VK_E:
			main.addZoom(0.9);
			break;
		case KeyEvent.VK_PLUS:
			main.addZoom(1.1);
			break;
		case KeyEvent.VK_MINUS:
			main.addZoom(0.9);
			break;
		case KeyEvent.VK_SPACE:
			rotateTile(1);
			break;
		case KeyEvent.VK_CONTROL:
			rotateTile(-1);
			break;
		case KeyEvent.VK_R:
			rotateTile(1);
			break;
		case KeyEvent.VK_F:
			rotateTile(-1);
			break;
		case KeyEvent.VK_H:
			selectTile(0);
			moveActiveTile(-1, 0);
			break;
		case KeyEvent.VK_J:
			scrollMenu(1);
			selectTile(1);
			moveActiveTile(0, 1);
			break;
		case KeyEvent.VK_K:
			scrollMenu(-1);
			selectTile(2);
			moveActiveTile(0, -1);
			break;
		case KeyEvent.VK_L:
			moveActiveTile(1, 0);
			break;
		case KeyEvent.VK_P:
			addTile();
			break;
		case KeyEvent.VK_U:
			skip();
			break;
		case KeyEvent.VK_O:
			turnOn();
			break;
		case KeyEvent.VK_I:
			project();
			break;
		case KeyEvent.VK_ESCAPE:
			main.getStateController().menu();
			break;
		case KeyEvent.VK_ENTER:
			selectMenu();
			clickCore();
			break;
		}

		main.setRefresh(0);
	}

	private void rotateTile(int ticks) {
		if (main.getActiveTile() != null)
			main.getActiveTile().rotate(ticks);
	}

	private void moveActiveTile(int x, int y) {
		Tile activeTile = main.getActiveTile();
		if (activeTile != null)
			activeTile.setCell(new Point(activeTile.getCell().x + x, activeTile.getCell().y + y));
	}

	private void addTile() {
		if (main.getActiveTile() != null) {
			Point p = main.getActiveTile().getPos();
			p = new Point((int) (p.x * main.getZoom()), (int) (p.y * main.getZoom()));
			main.getStateController().nextState(p.x, p.y);
		}
	}

	private void selectMenu() {
		if (main.getState() == GameState.MENU)
			main.getMenu().selectMenu();
	}

	private void clickCore() {
		if (main.getState() == GameState.MENU)
			return;
		Point p = (main.getGrid().grid(main.getGrid().getCenter()));
		p = new Point((int) ((p.x + main.getxCam()) * main.getZoom() + main.WIDTH / 2),
				(int) ((p.y + main.getyCam()) * main.getZoom() + main.HEIGHT / 2));
		main.getStateController().nextState(p.x, p.y);
	}

	private void selectTile(int index) {
		GameState state = main.getState();
		if (state == GameState.SELECT_SIDE || state == GameState.SELECT_PLAYERS || state == GameState.MARKET)
			if (index < main.getTileMarket().size()/(state==GameState.SELECT_PLAYERS?3:1)) {
				Tile t = main.getTileMarket().get(index);
				main.getStateController().nextState(t.getPos().x, t.getPos().y);
			}
		if (state == GameState.PROJECT)
				if (index < 2) {
					Tile t = main.getPlayerController().getActivePlayer().getProjectMarket().get(index);
					main.getStateController().nextState(t.getPos().x, t.getPos().y);
				}
	}

	private void moveCam(int x, int y) {
		main.addXCam(x * main.WIDTH / 10);
		main.addYCam(y * main.WIDTH / 10);
	}

	private void scrollMenu(int index) {
		if (main.getState() == GameState.MENU)
			main.getMenu().scrollMenu(index);
	}
	
	private void skip(){
		if(main.getPlayerController().getNumberOfPlayer()!=0){
			 Rectangle r = main.getPlayerController().getPowerSolderClickArea();
			 main.getStateController().nextState((int)r.getCenterX(), (int)r.getCenterY());
			}
	}
	private void turnOn(){
		if(main.getPlayerController().getNumberOfPlayer()!=0){
			 Rectangle r = main.getPlayerController().getActivePlayer().getSwitch().getClickArea();
			 main.getStateController().nextState((int)r.getCenterX(), (int)r.getCenterY());
			}
	}
	private void project(){
		if(main.getPlayerController().getNumberOfPlayer()!=0){
			 Rectangle r = main.getPlayerController().getActivePlayerClickArea();
			 main.getStateController().nextState((int)r.getCenterX(), (int)r.getCenterY());
			}
	}

}
