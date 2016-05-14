package tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Texture;

public class BasicTile {
	protected int tileNumber;
	protected Side side;
	protected Texture texture;
	protected Interface[] interf=new Interface[6];/* = { new Interface(IO.INPUT, Device.CONST0), new Interface(IO.INPUT, Device.CONST0),
			new Interface(IO.INPUT, Device.CONST0), new Interface(IO.INPUT, Device.CONST0),
			new Interface(IO.INPUT, Device.CONST0), new Interface(IO.INPUT, Device.CONST0) };;
	*/private InterfaceLoader intLoader;

	public BasicTile(int tileNumber, Side side, Texture texture, InterfaceLoader intLoader) {
		this.tileNumber = tileNumber;
		this.side = side;
		this.texture = texture;
		this.intLoader = intLoader;
		this.interf = intLoader.fetchInterfaces(tileNumber,side);
	}

	public int getTileNumber() {
		return tileNumber;
	}

	public void setTileNumber(int tileNumber) {
		this.tileNumber = (tileNumber + 72) % 72;
		interf = intLoader.fetchInterfaces(this.tileNumber,side);
	}

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
		interf = intLoader.fetchInterfaces(tileNumber,side);
	}

	public Interface[] getInterf() {
		return interf;
	}

	public void render(Graphics2D g2d) {
		BufferedImage image = fetchTexture();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}

	protected BufferedImage fetchTexture() {
		int col = tileNumber % Texture.COLS;
		int row = tileNumber / Texture.COLS;
		return texture.getTile(col, row, side);
	}

	public void setInterf(Interface[] interf) {
		this.interf = interf;
	}

	public ArrayList<Integer> whereIsThis(Interface i) {
		ArrayList<Integer> ans = new ArrayList<Integer>();
		for (int j = 0; j < 6; j++)
			if (interf[j].equals(i)) {
				ans.add(j);
			}
		return ans;
	}
	
	public ArrayList<Integer> getGates(IO dir){
		ArrayList<Integer> gates = new ArrayList<Integer>();
		for (int i=0;i<6;i++)
		{
			Interface in=interf[i];
			IO io=in.getIo();
			Device device=in.getDevice();
			if((io==dir||io==IO.BOTH)&&
					device!=Device.CORE&&
					device!=Device.WIREA&&
					device!=Device.WIREB&&
					device!=Device.WIREC&&
					device!=Device.WIREAOUTB&&
					device!=Device.WIREBOUTC&&
					device!=Device.WIRECOUTA&&
					device!=Device.CONST0&&
					device!=Device.CONST1)
					gates.add(i);
		}
		return gates;
	}
	
	public ArrayList<Integer> getGates(){
		return getGates(IO.BOTH);
	}
		
}
