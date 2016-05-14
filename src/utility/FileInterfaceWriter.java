package utility;

import tiles.Device;
import tiles.IO;
import tiles.Interface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileInterfaceWriter {/*
	public static void main(String[] args) {
		ArrayList<Interface[]> interfaceList= new ArrayList<Interface[]>();
		Interface[] interfSample=new Interface[6];
		for (int i=0;i<6;i++)
			interfSample[i]=new Interface(IO.INPUT, Device.CONST0);
		
		for(int i=0;i<72;i++)
			interfaceList.add(interfSample.clone());
		try {
			FileOutputStream file = new FileOutputStream("res/interfacesF.dat");
			ObjectOutputStream save = new ObjectOutputStream(file);
			save.writeObject(interfaceList);
			save.close();
			file = new FileOutputStream("res/interfacesB.dat");
			save = new ObjectOutputStream(file);
			save.writeObject(interfaceList);
			save.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}*/
}
