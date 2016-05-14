package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import tiles.Device;
import tiles.IO;
import tiles.Interface;

public class InterfacePanel extends JPanel {
	private JComboBox IObox;
	private JComboBox DeviceBox;
	/**
	 * Create the panel.
	 */
	public InterfacePanel() {
		setAlignmentY(Component.TOP_ALIGNMENT);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMinimumSize(new Dimension(0, 0));
		setBounds(new Rectangle(0, 0, 80, 40));
		setLayout(null);
		IObox = new JComboBox();
		IObox.setBounds(new Rectangle(0, 0, 50, 20));
		IObox.setModel(new DefaultComboBoxModel(IO.values()));
		IObox.setBounds(0, 0, 80, 20);
		add(IObox);
		
		DeviceBox = new JComboBox();
		DeviceBox.setBounds(new Rectangle(0, 0, 50, 20));
		DeviceBox.setModel(new DefaultComboBoxModel(Device.values()));
		DeviceBox.setBounds(0, 20, 80, 20);
		add(DeviceBox);

	}
	public void setInteface(Interface interf){
		IObox.setSelectedIndex(interf.getIo().ordinal());
		DeviceBox.setSelectedIndex(interf.getDevice().ordinal());
	}
	
	public Interface getInterface(){
		Interface interf = new Interface((IO)IObox.getSelectedItem(), (Device)DeviceBox.getSelectedItem());
		return interf;
	}

}
