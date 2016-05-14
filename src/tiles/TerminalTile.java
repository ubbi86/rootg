package tiles;

import java.awt.Color;

import main.Main;

public class TerminalTile extends Tile {
	private int speed;
	private Color color;
	/*private Interface[] terminalInterfacesFront={
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.WIREA),
			new Interface(IO.INPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.CORE)
			};
	private Interface[] terminalInterfacesBack={
			new Interface(IO.OUTPUT, Device.CORE),
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.INPUT, Device.WIREA),
			new Interface(IO.OUTPUT, Device.CONST0),
			new Interface(IO.OUTPUT, Device.CORE)
			};*/
	public TerminalTile(int tileNumber, Main main) {
		this(0, 0, tileNumber, 1, main);
	}
		
	public TerminalTile(int xPos, int yPos, int tileNumber, double size, Main main) {
		super(xPos, yPos, tileNumber, size, main);
		if(tileNumber<=13)
			speed=3;
		else if (tileNumber<=16)
			speed=2;
		else if (tileNumber<=19)
			speed=1;
		if(tileNumber%3==2)
			color=Color.RED;
		else if(tileNumber%3==0)
			color=Color.GREEN;
		else 
			color=Color.BLUE;
		//super.interf=(main.side==Side.FRONT?terminalInterfacesFront:terminalInterfacesBack);
	}

	public int getSpeed() {
		return speed;
	}

	public Color getColor() {
		return color;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getCoreSide(){
		return whereIsThis(new Interface(IO.BOTH, Device.CORE)).get(0);
	}

}
