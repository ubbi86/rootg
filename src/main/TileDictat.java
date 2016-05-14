package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tiles.BasicTile;
import tiles.Interface;
import tiles.InterfaceLoader;
import tiles.Side;
import javax.swing.JLabel;
import java.awt.Font;

public class TileDictat extends JFrame {

	private JPanel contentPane;
	private Canvas canvas;
	InterfacePanel[] interfacePanel;
	JLabel tileNumber;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TileDictat frame = new TileDictat();
					frame.setVisible(true);
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private Texture tex;
	private BasicTile tile;
	private InterfaceLoader intLoader;

	public TileDictat() {

		tex = new Texture();
		intLoader = new InterfaceLoader();
		// intLoader.setTerminalInterfaces(Side.FRONT);
		tile = new BasicTile(0, Side.FRONT, tex, intLoader);
		setPreferredSize(new Dimension(600, 500));
		setSize(new Dimension(600, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setSize(new Dimension(600, 500));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		contentPane.add(panel);
		panel.setLayout(null);

		interfacePanel = new InterfacePanel[6];
		HexGrid grid = new HexGrid(80);
		Point[] n = grid.neightbours(new Point(1, 1));
		for (int i = 0; i < 6; i++) {
			interfacePanel[i] = new InterfacePanel();
			interfacePanel[i].addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					writeInterfaceToTile();
				}
			});
			n[i] = grid.grid(n[i].x, n[i].y);
			interfacePanel[i].setBounds(n[i].x + 40, n[i].y, 80, 40);
			panel.add(interfacePanel[i]);
		}

		canvas = new Canvas() {
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				setBackground(Color.WHITE);
				tile.render((Graphics2D) getGraphics());
			}
		};
		canvas.setBackground(Color.WHITE);
		canvas.setBounds(218, 138, 142, 141);
		panel.add(canvas);

		JButton next = new JButton(">>");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				writeInterfaceToTile();
				intLoader.saveInterfaces(tile.getTileNumber(), tile.getInterf(), tile.getSide());
				tile.setTileNumber(tile.getTileNumber() + 1);
				tileNumber.setText(Integer.toString(tile.getTileNumber()));
				readInterfaceFromTile();
				canvas.paint(canvas.getGraphics());
			}
		});
		next.setBounds(475, 172, 89, 64);
		panel.add(next);

		JButton prev = new JButton("<<");
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				writeInterfaceToTile();
				intLoader.saveInterfaces(tile.getTileNumber(), tile.getInterf(), tile.getSide());
				tile.setTileNumber(tile.getTileNumber() - 1);
				tileNumber.setText(Integer.toString(tile.getTileNumber()));
				readInterfaceFromTile();
				canvas.paint(canvas.getGraphics());
			}
		});
		prev.setBounds(10, 172, 89, 64);
		panel.add(prev);

		JButton btnFlip = new JButton("FLIP");
		btnFlip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Side newSide = (tile.getSide() == Side.BACK ? Side.FRONT : Side.BACK);
				writeInterfaceToTile();
				intLoader.saveInterfaces(tile.getTileNumber(), tile.getInterf(), tile.getSide());
				tile.setSide(newSide);
				readInterfaceFromTile();
				canvas.paint(canvas.getGraphics());
			}
		});
		btnFlip.setBounds(218, 390, 142, 50);
		panel.add(btnFlip);
		
		tileNumber = new JLabel("0");
		tileNumber.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tileNumber.setBounds(518, 426, 46, 14);
		panel.add(tileNumber);

	}

	private void readInterfaceFromTile() {
		Interface[] interf = tile.getInterf();
		for (int i = 0; i < 6; i++) {
			interfacePanel[i].setInteface(interf[i]);
		}
	}

	private void writeInterfaceToTile() {
		Interface[] interf = new Interface[6];
		for (int i = 0; i < 6; i++) {
			interf[i] = interfacePanel[i].getInterface();
		}
		tile.setInterf(interf);
	}
}
