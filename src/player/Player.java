package player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;

import main.Main;
import tiles.ProjectTile0;
import tiles.ProjectTile1;
import tiles.Side;
import tiles.TerminalTile;
import tiles.TerminalTileStock;
import tiles.Tile;
import tiles.TileMarket;
import utility.QRGen;

public class Player implements Comparable<Player>{
	private Color color;
	private int chargeTokens;
	private boolean powerSolder;
	private TileMarket tileMarket;
	private TileMarket projectMarket;
	private Tile tile;
	private boolean[] dischTerminal=new boolean[3];
	private int[] circuitDelays=new int[3];
	private boolean[] project=new boolean[3];
	private Switch sw;
	private int score;
	private BufferedImage qrProject;
	
	public Player(Color color, int chargeTokens, Main main) {
		this.color=color;
		this.chargeTokens=chargeTokens;
		this.powerSolder=false;
		this.tile=null;
		tileMarket=new TileMarket(main);
		projectMarket=new TileMarket(main);
		sw=new Switch(color, false, main);
		ArrayList<Tile> terminalTiles = new ArrayList<Tile>();
		for (TerminalTile t : main.getTermStock().getTerminalTiles(color)) {
			terminalTiles.add(t);
		}
		tileMarket.populate(terminalTiles);
	}

	public void setProject(ProjectTile0 p0,ProjectTile1 p1){
		for (int speed=1;speed<4;speed++)
			project[speed-1]=p1.getValue(p0.getLetter(color, speed));
		projectMarket.clear();
		projectMarket.add(p0);
		projectMarket.add(p1);
		String projectString="Fast: "+(getProject(3)?1:0)
				+"\nNorm: "+(getProject(2)?1:0)
				+"\nSlow: "+(getProject(1)?1:0);
		try {
			qrProject=QRGen.createQRImage(projectString, 200, color);
		} catch (WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void hideProject(){
		projectMarket.get(0).setSide(Side.FRONT);
		projectMarket.get(1).setSide(Side.FRONT);
		projectMarket.setRefresh();
	}
	
	public boolean getProject(int speed){
		return project[speed-1];
	}
	
	public Color getColor() {
		return color;
	}

	public int getChargeTokens() {
		return chargeTokens;
	}

	public boolean hasPowerSolder() {
		return powerSolder;
	}
	
	public TileMarket getTileMarket() {
		return tileMarket;
	}
	public TileMarket getProjectMarket() {
		return projectMarket;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public void setChargeTokens(int chargeTokens) {
		this.chargeTokens = chargeTokens;
	}
	
	public void setTileMarket(TileMarket tileMarket) {
		this.tileMarket = tileMarket;
	}

	public void setPowerSolder(boolean powerSolder) {
		this.powerSolder = powerSolder;
	}
	
	public void addCharge(int i){
		this.chargeTokens+=i;
	}
	
	public void dropCharge(int i){
		this.chargeTokens-=i;
		if (chargeTokens<0) chargeTokens=0;
	}
	
	public void tick(){
		tileMarket.tick();
		projectMarket.tick();
	}
	
	public void render(Graphics2D g2d){
		tileMarket.render(g2d);
	}

	@Override
	public int compareTo(Player p) {
		int[] value=new int[2];
		Player[] pS=new Player[2];
		pS[0]=this;
		pS[1]=p;
		for(int i=0;i<2;i++){
			value[i]=(pS[i].powerSolder?1:0)-pS[i].getScore()*125000;
			for(int j=0; j<3; j++)
				value[i]+=pS[i].getCircDelay(j+1)*Math.pow(50, j);
		}
		return value[0]-value[1];
	}

	public Tile getTile() {
		return tile;
	}

	public boolean setTile(Tile tile) {
		if (tile==null)
			return false;
		this.tile = tile;
		return true;
	}
	
	public void discharge(){
		for(int i=0;i<3;i++){
			if (dischTerminal[i])
				dropCharge(i+1);
			dischTerminal[i]=false;
			}
	}
	
	public void setDischarge(int speed){
		dischTerminal[speed-1]=true;
	}

	
	public void setCircDelay(int speedTerminal, int delay){
		circuitDelays[speedTerminal-1]=delay;
	}
	
	public int getCircDelay(int speedTerminal){
		return circuitDelays[speedTerminal-1];
	}

	public Switch getSwitch() {
		return sw;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public BufferedImage getQrProject() {
		return qrProject;
	}
}
