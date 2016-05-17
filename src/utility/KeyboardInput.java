package utility;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.Main;

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
			main.addXCam(main.WIDTH / 10);
			break;
		case KeyEvent.VK_RIGHT:
			main.addXCam(-main.WIDTH / 10);
			break;
		case KeyEvent.VK_UP:
			main.addYCam(main.WIDTH / 10);
			break;
		case KeyEvent.VK_DOWN:
			main.addYCam(-main.WIDTH / 10);
			break;
		case KeyEvent.VK_A:
			main.addXCam(main.WIDTH / 10);
			break;
		case KeyEvent.VK_D:
			main.addXCam(-main.WIDTH / 10);
			break;
		case KeyEvent.VK_W:
			main.addYCam(main.WIDTH / 10);
			break;
		case KeyEvent.VK_S:
			main.addYCam(-main.WIDTH / 10);
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
			if(main.getActiveTile()!=null)
				main.getActiveTile().rotate(1);
				break;
		case KeyEvent.VK_CONTROL:
			if(main.getActiveTile()!=null)
				main.getActiveTile().rotate(-1);
				break;
		}

		main.setRefresh(0);
	}

}
