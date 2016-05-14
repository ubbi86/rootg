package utility;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import main.Main;
import tiles.Tile;

public class HelperCursor extends Spark {
	protected boolean frameRel;
	public HelperCursor(Tile tile, Main main) {
		super(tile, 0, 5, true, main, true);
		super.minSpeed = 10;
		super.maxSpeed = 50;
		// super.setPos(new Point(main.WIDTH/3,main.HEIGHT/3));
	}

	public void render(Graphics2D g2d) {
		if (!frameRel)
			main.camEngine(true, g2d);
		double size = 50 + super.getCharge() * 15;
		g2d.drawImage(super.main.getTexture().getSpark(), (int) (renderPos.getX() - size / 2),
				(int) (renderPos.getY() - size / 2), (int) size, (int) size, null);
		if (!frameRel)
			main.camEngine(false, g2d);
	}

	public boolean isClicked(Point p) {
		double size = 30 + super.getCharge() * 15;
		if (!frameRel)
			p = new Point((int) ((p.getX() - main.WIDTH / 2) / main.getZoom() - main.getxCam()),
					(int) ((p.getY() - main.HEIGHT / 2) / main.getZoom() - main.getyCam()));
			
		return new Rectangle((int) (pos.getX() - size / 2), (int) (pos.getY() - size / 2), (int) size,
				(int) size).contains(p);
	}

	public void setFrameRel(boolean frameRel) {
		this.frameRel = frameRel;
	}

	@Override
	public void tick() {
		if (super.getTile()!=null) setPos(super.getTile());
		super.tick();
	}

	@Override
	public void setPos(Tile tile) {
		super.setPos(tile);
		while(super.trajectory.size()>1)
			super.trajectory.remove(0);
	}

	@Override
	public void setPos(Point p) {
		super.setPos(p);
		while(super.trajectory.size()>1)
			super.trajectory.remove(0);
	}
}
