package utility;

import java.util.ArrayList;

import main.Texture;
import tiles.BasicTile;
import tiles.Device;
import tiles.IO;
import tiles.Interface;
import tiles.InterfaceLoader;
import tiles.Side;
import tiles.TerminalTile;
import tiles.Tile;
import tiles.TileController;

public class WireChaser {
	static ArrayList<Integer> sidesOut(BasicTile t, int sideIn) {
		ArrayList<Integer> sidesOut = new ArrayList<Integer>();
		Interface[] interf = t.getInterf();
		if (interf[sideIn].getIo() != IO.OUTPUT) {
			ArrayList<Interface> targetInterf = new ArrayList<Interface>();
			switch (interf[sideIn].getDevice()) {
			case MUX1:
				targetInterf.add(new Interface(IO.OUTPUT, Device.MUX0));
				break;
			case DEMUX0:
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX1));
				break;
			case SELECTOR:
				targetInterf.add(new Interface(IO.OUTPUT, Device.MUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX1));
				break;
			case WIREAOUTB:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIRECOUTA));
				break;
			case WIREBOUTC:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREAOUTB));
				break;
			case WIRECOUTA:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREBOUTC));
				break;
			case WIREA:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIRECOUTA));
				break;
			case WIREB:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREAOUTB));
				break;
			case WIREC:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREBOUTC));
				break;
			default:
				targetInterf.add(new Interface(IO.OUTPUT, interf[sideIn].getDevice()));
			}
			for (Interface i : targetInterf) {
				sidesOut.addAll(t.whereIsThis(i));
			}
			sidesOut.remove((Integer) sideIn);
		}
		return sidesOut;
	}

	static public ArrayList<Integer> sidesOut(Tile t, int sideIn) {
		ArrayList<Integer> sidesOut = new ArrayList<Integer>();
		Interface[] interf = t.getInterf();
		if (interf[sideIn].getIo() != IO.OUTPUT) {
			ArrayList<Interface> targetInterf = new ArrayList<Interface>();
			switch (interf[sideIn].getDevice()) {
			case MUX1:
				targetInterf.add(new Interface(IO.OUTPUT, Device.MUX0));
				break;
			case DEMUX0:
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX1));
				break;
			case SELECTOR:
				targetInterf.add(new Interface(IO.OUTPUT, Device.MUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX0));
				targetInterf.add(new Interface(IO.OUTPUT, Device.DEMUX1));
				break;
			case WIREAOUTB:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIRECOUTA));
				break;
			case WIREBOUTC:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREAOUTB));
				break;
			case WIRECOUTA:
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREBOUTC));
				break;
			case WIREA:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIRECOUTA));
				break;
			case WIREB:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREAOUTB));
				break;
			case WIREC:targetInterf.add(new Interface(IO.OUTPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.OUTPUT, Device.WIREBOUTC));
				break;
			default:
				targetInterf.add(new Interface(IO.OUTPUT, interf[sideIn].getDevice()));
			}
			for (Interface i : targetInterf) {
				sidesOut.addAll(t.whereIsThis(i));
			}
			sidesOut.remove((Integer) sideIn);
		}
		return sidesOut;
	}

	static public ArrayList<Integer> sidesIn(Tile t, int sideOut) {
		ArrayList<Integer> sidesIn = new ArrayList<Integer>();
		Interface[] interf = t.getInterf();
		if (interf[sideOut].getIo() != IO.INPUT) {
			ArrayList<Interface> targetInterf = new ArrayList<Interface>();
			switch (interf[sideOut].getDevice()) {
			case MUX0:
				targetInterf.add(new Interface(IO.INPUT, Device.MUX0));
				targetInterf.add(new Interface(IO.INPUT, Device.MUX1));
				break;
			case DEMUX1:
				targetInterf.add(new Interface(IO.INPUT, Device.DEMUX0));
				break;
			case SELECTOR:
				targetInterf.add(new Interface(IO.INPUT, Device.DEMUX0));
				targetInterf.add(new Interface(IO.INPUT, Device.MUX0));
				targetInterf.add(new Interface(IO.INPUT, Device.MUX1));
				break;
			case WIREAOUTB:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.INPUT, Device.WIREBOUTC));
				break;
			case WIREBOUTC:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.INPUT, Device.WIRECOUTA));
				break;
			case WIRECOUTA:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.INPUT, Device.WIREAOUTB));
				break;
			case WIREA:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREA));
				targetInterf.add(new Interface(IO.INPUT, Device.WIREAOUTB));
				break;
			case WIREB:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREB));
				targetInterf.add(new Interface(IO.INPUT, Device.WIREBOUTC));
				break;
			case WIREC:
				targetInterf.add(new Interface(IO.INPUT, Device.WIREC));
				targetInterf.add(new Interface(IO.INPUT, Device.WIRECOUTA));
				break;
			default:
				targetInterf.add(new Interface(IO.INPUT, interf[sideOut].getDevice()));
			}
			for (Interface i : targetInterf) {
				sidesIn.addAll(t.whereIsThis(i));
			}
			sidesIn.remove((Integer) sideOut);
		}
		return sidesIn;
	}

	public static ArrayList<TerminalTile> chaseInOut(TileController tc, Tile tile) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		ArrayList<Integer> outGatesSides = tile.getGates(IO.INPUT);
		if (outGatesSides.size() != 0)
			ans.addAll(chaseInOut(tc, tile, outGatesSides.get(0)));
		return ans;
	}

	public static ArrayList<TerminalTile> chaseInOut(TileController tc, Tile tile, int sideIn) {
		return chaseInOut(tc, tile, sideIn, new ArrayList<Tile>());
	}

	public static ArrayList<TerminalTile> chaseInOut(TileController tc, Tile tile, int sideIn,
			ArrayList<Tile> tilePath) {
		ArrayList<TerminalTile> ans = new ArrayList<TerminalTile>();
		tilePath.add(tile);
		if (tile.whereIsThis(new Interface(IO.BOTH, Device.CORE)).size() != 0) {
			ans.add((TerminalTile) tile);
			for (Tile t : tilePath)
				t.setSize(1.2);
		}
		ArrayList<Integer> sidesOut = sidesOut(tile, sideIn);
		for (Integer i : sidesOut) {
			Tile nextTile = tc.getFacingTile(tile, i);
			if (nextTile != null) {
				if (tc.getFacingInterface(tile, i).getIo() != IO.OUTPUT) {
					if (nextTile.whereIsThis(new Interface(IO.OUTPUT, Device.CORE)).size() > 0) {
						TerminalTile tTile = (TerminalTile) nextTile;
						tilePath.add(nextTile);
						for (Tile t : tilePath) {
							t.setSize(1.2);
							t.setMask(tTile.getColor());
						}
						ans.add(tTile);
						// tc.getMain().getPlayerController().getPlayerByColor(tTile.getColor()).setDischarge(tTile.getSpeed());
					} else {
						ans.addAll(chaseInOut(tc, nextTile, Tile.opposite(i), (ArrayList<Tile>) tilePath.clone()));
					}
				}
			}
		}
		return ans;
	}

	public static int calcDelay(TileController tc, TerminalTile tTile) {
		int delay = 0;
		int sideIn = tTile.whereIsThis(new Interface(IO.INPUT, Device.WIREA)).get(0);
		Tile nextTile = tc.getFacingTile(tTile, sideIn);
		if (nextTile != null) {
			if (tc.getFacingInterface(tTile, sideIn).isOutGate())
				delay++;
			ArrayList<Integer> sidesIn = sidesIn(nextTile, Tile.opposite(sideIn));
			int delayCopy = delay;
			for (Integer i : sidesIn) {
				delay = Math.max(delay, calcDelay(tc, nextTile, i, delayCopy));
			}
		}
		return delay;
	}

	public static int calcDelay(TileController tc, Tile t, int sideIn, int delay) {
		Tile nextTile = tc.getFacingTile(t, sideIn);
		if (nextTile != null) {
			if (tc.getFacingInterface(t, sideIn).isOutGate())
				delay++;
			ArrayList<Integer> sidesIn = sidesIn(nextTile, Tile.opposite(sideIn));
			int delayCopy = delay;
			for (Integer i : sidesIn) {
				delay = Math.max(delay, calcDelay(tc, nextTile, i, delayCopy));
			}
		}
		return delay;
	}

	public static void dischargeTerminals(TileController tc, Tile tile) {
		ArrayList<TerminalTile> terminals = chaseInOut(tc, tile);
		for (TerminalTile tTile : terminals)
			tc.getMain().getPlayerController().getPlayerByColor(tTile.getColor()).setDischarge(tTile.getSpeed());

	}

	public static boolean gateComplete(Tile t, SparkController sc) {
		ArrayList<Integer> inGates = t.getGates(IO.INPUT);
		if (inGates.size() == 0)
			return false;
		for (Integer i : inGates)
			if (!sc.isSparked(t, i))
				return false;
		return true;
	}
	public static boolean isGate(Device device){
		return (device!=Device.CORE&&
				device!=Device.WIREA&&
				device!=Device.WIREB&&
				device!=Device.WIREC&&
				device!=Device.WIREAOUTB&&
				device!=Device.WIREBOUTC&&
				device!=Device.WIRECOUTA&&
				device!=Device.CONST0&&
				device!=Device.CONST1);
	}

	/*public static void main(String[] args) {
		Texture tex = new Texture();
		BasicTile t = new BasicTile(45, Side.BACK, tex, new InterfaceLoader());
		ArrayList<Integer> outGatesSides = t.getGates(IO.INPUT);
		for (Integer j : outGatesSides) {
			ArrayList<Integer> ans = WireChaser.sidesOut(t, j);
			System.out.println(t.getInterf()[j].getDevice());
			System.out.println(ans);
		}
		System.out.println(false^true);
	}*/
}
