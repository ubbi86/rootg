package utility;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

import main.GameState;
import main.Main;
import tiles.Tile;

public class MouseInput implements MouseInputListener, MouseWheelListener {

	private Main main;
	private boolean midClick = false;
	private boolean rightClick = false;
	private Point startDrag;

	public MouseInput(Main main) {
		this.main = main;
		main.addMouseListener(this);
		main.addMouseMotionListener(this);
		main.addMouseWheelListener(this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		startDrag = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON2) {
			midClick = false;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			main.getStateController().nextState(e.getX(), e.getY());
		startDrag = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON2) {
			midClick = true;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick = true;
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		moveActiveTile(e.getPoint());
		menuHighlight(e.getPoint());
		}

	@Override
	public void mouseWheelMoved(MouseWheelEvent wheelEvent) {
		if (main.getActiveTile() == null)
			return;
		main.getActiveTile().rotate(-wheelEvent.getWheelRotation());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3)
			main.getStateController().menu();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (midClick) {
			main.addXCam((int) (e.getX() - startDrag.getX()));
			main.addYCam((int) (e.getY() - startDrag.getY()));
			startDrag = e.getPoint();
			main.setRefresh();
		}

		if (rightClick) {
			double y0 = startDrag.getY();
			double y = e.getY();
			main.addZoom((y0 - y) / y0 + 1);
			startDrag = e.getPoint();
			main.setRefresh();

		}
		moveActiveTile(e.getPoint());
	}

	private void moveActiveTile(Point p) {
		Tile activeTile = main.getActiveTile();
		if (activeTile != null)
			activeTile.setPos(new Point((int) (p.getX() / main.getZoom()), (int) (p.getY() / main.getZoom())));
	}
	
	private void menuHighlight(Point p){
		if (main.getState() == GameState.MENU)
			main.getMenu().setHighlight(p);
	}
}
