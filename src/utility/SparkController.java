package utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import main.Main;
import player.Player;
import player.PlayerController;
import tiles.Device;
import tiles.IO;
import tiles.Interface;
import tiles.TerminalTile;
import tiles.Tile;
import tiles.TileController;

public class SparkController extends ArrayList<Spark> {

	private ArrayList<Spark> buffSparks;
	private ArrayList<Spark> targetSparks;
	private Main main;
	int charge;
	private int coreServed;
	boolean firstClock = true;

	// CONSTRUCTORS
	public SparkController(Main main) {
		this.main = main;
		targetSparks = new ArrayList<Spark>();
		firstClock = true;
	}

	// GETTERS&SETTERS
	public void setRemove(Tile t, int side) {
		get(t, side).setRemove();
	}

	public void setRemove(TerminalTile tTile) {
		get(tTile).setRemove();
	}

	public boolean isSparked(Tile t, int side) {
		return get(t, side) != null;
	}

	public Spark get(Tile t, int side) {
		for (int i = buffSparks.size() - 1; i >= 0; i--) {
			Spark s = buffSparks.get(i);
			if (s.getTile() == t && s.getSide() == side)
				return s;
		}
		return null;
	}

	public Spark get(TerminalTile tTile) {
		for (int i = targetSparks.size() - 1; i >= 0; i--) {
			Spark s = targetSparks.get(i);
			if (s.getTile() == tTile)
				return s;
		}
		return null;
	}

	public boolean allProcessed() {
		return coreServed == 6;
	}

	// METHODS
	public void add(Tile t, int side, boolean logicValue) {
		add(new Spark(t, side, charge, logicValue, main));
	}

	public void addTargetSparks() {
		PlayerController pc = main.getPlayerController();
		TileController tc = main.getTc();
		TerminalTile[] core = tc.getCore();
		for (int i = 0; i < core.length; i++) {
			TerminalTile tTile = core[i];
			targetSparks.add(new Spark(tTile, tTile.getCoreSide(), 5,
					pc.getPlayerByColor(tTile.getColor()).getProject(tTile.getSpeed()), main, false));
		}
	}

	public void moveSparks() {
		charge++;
		boolean gateReached = false;
		buffSparks = (ArrayList<Spark>) super.clone();
		if (!firstClock)
			for (int i = 0; i < buffSparks.size(); i++)
				passGate(buffSparks.get(i));
		firstClock = false;
		for (int i = 0; i < size(); i++)
			gateReached |= moveToNextTile(get(i));
		if (gateReached)
			charge = 0;
		for (Spark s : this)
			s.setCharge(charge);
	}

	public boolean moveToNextTile(Spark s) {
		if (s.getRemove())
			return false;
		Tile tile = s.getTile();
		if (main.getTc().isTerminal(tile)) {
			/// SIGNAL PROCESSED
			processSignal((TerminalTile) tile, s);
			s.setRemove();
			return true;
		}
		int side = s.getSide();
		if (WireChaser.isGate(tile.getInterface(side).getDevice())) {// getGates(IO.INPUT).indexOf(side)
			return false;
		}
		// s.setPos(tile);
		ArrayList<Integer> sidesOut = WireChaser.sidesOut(tile, side);
		if (sidesOut.size() == 0)
			return false;
		s.setPos(tile, sidesOut.get(0));
		for (int i = 1; i < sidesOut.size(); i++) {
			Spark sClone;
			try {
				sClone = s.clone();

				add(sClone);
				Tile facingT = main.getTc().getFacingTile(tile, sidesOut.get(i));
				Interface facingInt = main.getTc().getFacingInterface(tile, sidesOut.get(i));
				if (facingT == null || facingInt.getIo() == IO.OUTPUT)
					sClone.setRemove();
				else {
					sClone.setPos(facingT, Tile.opposite(sidesOut.get(i)));
					moveToNextTile(sClone);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		Tile facingT = main.getTc().getFacingTile(tile, sidesOut.get(0));
		// System.out.println(facingT);
		Interface facingInt = main.getTc().getFacingInterface(tile, sidesOut.get(0));
		// System.out.println(facingInt.getDevice()+" "+facingInt.getIo());
		if (facingT == null || facingInt.getIo() == IO.OUTPUT)
			s.setRemove();
		else {
			s.setPos(facingT, Tile.opposite(sidesOut.get(0)));
			moveToNextTile(s);
		}
		return false;
	}

	public void passGate(Spark s) {
		if (s.getRemove()) {
			return;
		}
		Tile tile = s.getTile();
		int side = s.getSide();
		if (!WireChaser.isGate(tile.getInterface(side).getDevice())) {// getGates(IO.INPUT).indexOf(side)
																		// > 0){
			return;
		}
		if (!WireChaser.gateComplete(tile, this)) {
			return;
		}
		Device dev = tile.getInterface(side).getDevice();
		ArrayList<Spark> sparksInvolved = new ArrayList<Spark>();
		boolean outputLogicalValue = false;
		if (tile.whereIsThis(new Interface(IO.INPUT, Device.DEMUX0)).size() > 0) {
			Spark selectorSpark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.SELECTOR)).get(0));
			sparksInvolved.add(selectorSpark);
			Spark demux0Spark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.DEMUX0)).get(0));
			sparksInvolved.add(demux0Spark);
			for (Spark ss : sparksInvolved) {
				ss.setPos(tile);
				ss.setRemove();
			}
			outputLogicalValue = (selectorSpark.getLogicValue() ? !demux0Spark.getLogicValue()
					: demux0Spark.getLogicValue());
			int demux0Side = tile.whereIsThis(new Interface(IO.OUTPUT, Device.DEMUX0)).get(0);
			int demux1Side = tile.whereIsThis(new Interface(IO.OUTPUT, Device.DEMUX1)).get(0);
			Spark outSpark0 = new Spark(tile, demux0Side, charge, outputLogicalValue, main, true);
			Spark outSpark1 = new Spark(tile, demux1Side, charge, !outputLogicalValue, main, true);
			add(outSpark0);
			add(outSpark1);
			outSpark0.setPos(tile, demux0Side);
			outSpark1.setPos(tile, demux1Side);
			Tile facingTile = main.getTc().getFacingTile(tile, demux0Side);
			if (facingTile == null)
				outSpark0.setRemove();
			else
				outSpark0.setPos(facingTile, Tile.opposite(demux0Side));

			facingTile = main.getTc().getFacingTile(tile, demux1Side);
			if (facingTile == null)
				outSpark1.setRemove();
			else
				outSpark1.setPos(facingTile, Tile.opposite(demux1Side));
		}

		else {
			ArrayList<Integer> inputSides = tile.whereIsThis(new Interface(IO.INPUT, dev));
			for (Integer i : inputSides)
				sparksInvolved.add(get(tile, i));
			switch (dev) {
			case BUF:
				outputLogicalValue = s.getLogicValue();
				break;

			case INV:
				outputLogicalValue = !s.getLogicValue();
				break;

			case AND:
				outputLogicalValue = sparksInvolved.get(0).getLogicValue() && sparksInvolved.get(1).getLogicValue();
				break;

			case OR:
				outputLogicalValue = sparksInvolved.get(0).getLogicValue() || sparksInvolved.get(1).getLogicValue();
				break;

			case XOR:
				outputLogicalValue = sparksInvolved.get(0).getLogicValue() ^ sparksInvolved.get(1).getLogicValue();
				break;

			case NAND:
				outputLogicalValue = !(sparksInvolved.get(0).getLogicValue() && sparksInvolved.get(1).getLogicValue());
				break;

			case NOR:
				outputLogicalValue = !(sparksInvolved.get(0).getLogicValue() || sparksInvolved.get(1).getLogicValue());
				break;
			}

			if (dev == Device.MUX0) {
				Spark selectorSpark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.SELECTOR)).get(0));
				sparksInvolved.add(selectorSpark);
				Spark mux0Spark = sparksInvolved.get(0);
				Spark mux1Spark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.MUX1)).get(0));
				sparksInvolved.add(mux1Spark);
				outputLogicalValue = (selectorSpark.getLogicValue() ? mux1Spark.getLogicValue()
						: mux0Spark.getLogicValue());
			}

			if (dev == Device.MUX1) {
				Spark selectorSpark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.SELECTOR)).get(0));
				sparksInvolved.add(selectorSpark);
				Spark mux1Spark = sparksInvolved.get(0);
				Spark mux0Spark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.MUX0)).get(0));
				sparksInvolved.add(mux1Spark);
				outputLogicalValue = (selectorSpark.getLogicValue() ? mux1Spark.getLogicValue()
						: mux0Spark.getLogicValue());
			}
			if (dev == Device.SELECTOR && tile.whereIsThis(new Interface(IO.INPUT, Device.MUX0)).size() > 0) {
				Spark mux0Spark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.MUX0)).get(0));
				sparksInvolved.add(mux0Spark);
				Spark mux1Spark = get(tile, tile.whereIsThis(new Interface(IO.INPUT, Device.MUX1)).get(0));
				sparksInvolved.add(mux1Spark);
				Spark selectorSpark = sparksInvolved.get(0);
				outputLogicalValue = (selectorSpark.getLogicValue() ? mux1Spark.getLogicValue()
						: mux0Spark.getLogicValue());
			}
			for (Spark ss : sparksInvolved) {
				ss.setPos(tile);
				ss.setRemove();
			}
			ArrayList<Integer> outSides = tile.getGates(IO.OUTPUT);

			for (Integer j : outSides) {
				Spark outSpark = new Spark(tile, j, charge, outputLogicalValue, main, true);
				add(outSpark);
				outSpark.setPos(tile, j);
				Tile facingTile = main.getTc().getFacingTile(tile, j);
				Interface facingInt = main.getTc().getFacingInterface(tile, j);
				if (facingTile == null || facingInt.getIo() == IO.OUTPUT)
					outSpark.setRemove();
				else
					outSpark.setPos(facingTile, Tile.opposite(j));
			}
		}
	}

	private void processSignal(TerminalTile tTile, Spark s) {
		coreServed++;
		Color color = tTile.getColor();
		tTile.setSize(1.5);
		// System.out.println("CORE REACHED");
		Player p = main.getPlayerController().getPlayerByColor(color);
		tTile.setMask(color, true);
		if (p.getProject(tTile.getSpeed()) == s.getLogicValue()) {
			p.addCharge(charge);
			get(tTile).setCharge(charge);
		} else
			remove(tTile);
		main.getPlayerController().setRefresh();
	}

	public boolean tick() {
		boolean ans=false;
		for (int i = 0; i < size(); i++) {
			Spark s = get(i);
			ans|=s.tick();
			if (s.remove())
				remove(s);
		}
		return ans;
	}

	public void render(Graphics2D g2d) {
		for (int i = 0; i < size(); i++)
			get(i).render(g2d);
		for (int i = 0; i < targetSparks.size(); i++) {
			Spark s = targetSparks.get(i);
			if (!s.getRemove())
				s.render(g2d);
		}
	}
}
