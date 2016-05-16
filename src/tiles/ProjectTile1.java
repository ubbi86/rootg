package tiles;

import main.Main;

public class ProjectTile1 extends Tile {
	private boolean[] values = new boolean[6];

	// CONSTRUCTORS
	public ProjectTile1(int tileNumber, Main main) {
		super(tileNumber, 1, main);
		setSide(Side.FRONT);

		switch (tileNumber) {
		case 0:
			values[0] = false;
			values[1] = false;
			values[2] = false;
			values[3] = true;
			values[4] = true;
			values[5] = true;

			break;
		case 1:
			values[0] = false;
			values[1] = true;
			values[2] = false;
			values[3] = false;
			values[4] = true;
			values[5] = true;
			break;
		case 2:
			values[0] = true;
			values[1] = false;
			values[2] = true;
			values[3] = true;
			values[4] = false;
			values[5] = false;
			break;
		}
	}

	// GETTERS&SETTERS
	public boolean getValue(char letter) {
		int pos = 0;
		switch (letter) {
		case 'A':
			pos = 0;
			break;
		case 'B':
			pos = 1;
			break;
		case 'C':
			pos = 2;
			break;
		case 'D':
			pos = 3;
			break;
		case 'E':
			pos = 4;
			break;
		case 'F':
			pos = 5;
			break;
		}
		return values[pos];
	}

	// METHODS
	public void show() {
		setSide(Side.FRONT);
		flip();
	}

	public void hide() {
		setSide(Side.BACK);
		flip();
	}
}
