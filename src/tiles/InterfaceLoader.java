package tiles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InterfaceLoader {
	private ArrayList<Interface[]> interfaceListF;
	private ArrayList<Interface[]> interfaceListB;

	// CONSTRUCTORS
	public InterfaceLoader() {
		interfaceListF = new ArrayList<Interface[]>();
		interfaceListB = new ArrayList<Interface[]>();
		loadInterfaces(Side.BACK);
		loadInterfaces(Side.FRONT);
	}

	private void loadInterfaces(Side side) {
		interfaceListF.clear();
		interfaceListB.clear();
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("interfacesF.dat");
			ObjectInputStream load = new ObjectInputStream(is);
			interfaceListF = (ArrayList<Interface[]>) load.readObject();
			load.close();
			is = classloader.getResourceAsStream("interfacesB.dat");
			load = new ObjectInputStream(is);
			interfaceListB = (ArrayList<Interface[]>) load.readObject();
			load.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// setTerminalInterfaces(side);
		// saveInterfaces();
	}

	public void saveInterfaces(int tileNumber, Interface[] interf, Side side) {
		if (side == Side.BACK) {
			interfaceListB.set(tileNumber, interf);
			saveInterfaces(side);
		}
		if (side == Side.FRONT) {
			interfaceListF.set(tileNumber, interf);
			saveInterfaces(side);
		}
	
	}

	public void saveInterfaces(Side side) {
		try {
			FileOutputStream file = new FileOutputStream("res/interfaces" + (side == Side.BACK ? "B" : "F") + ".dat");
			ObjectOutputStream save = new ObjectOutputStream(file);
			save.writeObject((side == Side.BACK ? interfaceListB : interfaceListF));
			save.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// METHODS
	public void setTerminalInterfaces() {
		Interface outZero = new Interface(IO.OUTPUT, Device.CONST0);
		Interface[] interf = new Interface[6];

		interf[0] = outZero;
		interf[1] = outZero;
		interf[2] = new Interface(IO.INPUT, Device.WIREA);
		interf[3] = outZero;
		interf[4] = outZero;
		interf[5] = new Interface(IO.OUTPUT, Device.CORE);
		for (int i = 11; i < 20; i++)
			interfaceListF.set(i, interf);

		interf[0] = new Interface(IO.OUTPUT, Device.CORE);
		interf[1] = outZero;
		interf[2] = outZero;
		interf[3] = new Interface(IO.INPUT, Device.WIREA);
		interf[4] = outZero;
		interf[5] = outZero;
		for (int i = 11; i < 20; i++)
			interfaceListB.set(i, interf);
	}

	public Interface[] fetchInterfaces(int tileNumber, Side side) {
		if (side == Side.BACK)
			return interfaceListB.get(tileNumber);
		else
			return interfaceListF.get(tileNumber);

	}

}
