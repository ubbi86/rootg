package utility;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import main.Main;
import tiles.Tile;

public class Spark implements Cloneable {
	private boolean logicValue;
	private int charge;
	protected Point pos=new Point();
	protected ArrayList<Point> trajectory;
	protected Point renderPos;
	private Tile tile;
	private int side;
	protected Main main;
	private boolean toBeRemoved;
	private boolean remove;

	protected int minSpeed = 5;
	protected int maxSpeed = 5;

	public Spark(Tile tile, int side, int charge, boolean one, Main main) {
		this(tile, side, charge, one, main, false);
	}

	public Spark(Tile tile, int side, int charge, boolean one, Main main, boolean center) {
		this.tile = tile;
		this.side = side;
		if (tile != null) {
			if (center)
				pos = new Point((int) (tile.getPos().getX()), (int) tile.getPos().getY());
			else {
				Point nextCell = main.getGrid().neightbours(tile.getCell())[side];
				Point p = main.getGrid().grid((int) nextCell.getX(), (int) nextCell.getY());
				pos = new Point((int) ((tile.getPos().getX() + p.getX()) / 2),
						(int) ((tile.getPos().getY() + p.getY()) / 2));
			}
		}
		this.renderPos = this.pos;
		this.charge = charge;
		this.logicValue = one;
		this.main = main;
		trajectory = new ArrayList<Point>();
	}

	public void tick() {
		boolean ans = false;
		if (trajectory.size() == 0) {
			remove = toBeRemoved;
			return;
		}
		Point target = trajectory.get(0);
		double distX = target.getX() - renderPos.getX();
		int newX = (int) target.getX();
		double distY = target.getY() - renderPos.getY();
		int newY = (int) target.getY();
		double sqD=distX*distX+distY*distY;
		double d=Math.sqrt(sqD);
		if (sqD > minSpeed*minSpeed){
			int speed=Math.min(maxSpeed,(int) Math.abs(d/3));
			newX = (int) (renderPos.getX() + speed*distX/d);
			newY = (int) (renderPos.getY() + speed*distY/d);}
		renderPos.setLocation(newX, newY);
		if (renderPos.getX() == target.getX() && renderPos.getY() == target.getY())
			trajectory.remove(0);
	}

	public void render(Graphics2D g2d) {
		double size = 50 + charge * 15;
		g2d.drawImage(main.getTexture().getSpark(logicValue), (int) (renderPos.getX() - size / 2),
				(int) (renderPos.getY() - size / 2), (int) size, (int) size, null);
	}

	public boolean isLogicValue() {
		return logicValue;
	}

	public void setLogicValue(boolean logicValue) {
		this.logicValue = logicValue;
	}

	public boolean getLogicValue() {
		return logicValue;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Tile tile, int side) {
		Point nextCell = main.getGrid().neightbours(tile.getCell())[side];
		Point p = main.getGrid().grid((int) nextCell.getX(), (int) nextCell.getY());
		pos = new Point((int) ((tile.getPos().getX() + p.getX()) / 2), (int) ((tile.getPos().getY() + p.getY()) / 2));
		this.tile = tile;
		this.side = side;
		trajectory.add(pos);
	}

	public void setPos(Tile tile) {
		pos = new Point((int) (tile.getPos().getX()), (int) tile.getPos().getY());
		this.tile = tile;
		trajectory.add(pos);
	}

	public void setPos(Point p) {
		pos = p;
		trajectory.add(pos);
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public void setRemove() {
		toBeRemoved = true;
	}

	public boolean getRemove() {
		return toBeRemoved;
	}

	public boolean remove() {
		return remove;
	}

	@Override
	protected Spark clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (Spark) super.clone();
	}
}
