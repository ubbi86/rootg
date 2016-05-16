package tiles;

import java.awt.Color;

import main.Main;

public class ProjectTile0 extends Tile {
	private Terminal[] projectLetters = new Terminal[9];

	// CONSTRUCTORS
	public ProjectTile0(int tileNumber, Main main) {
		super(tileNumber, 1, main);
		setSide(Side.FRONT);
		Color[] colorList = { Color.RED, Color.GREEN, Color.BLUE };
		String letters;
		switch (tileNumber) {
		case 3:
			letters = "BFCEAFFCD";
			break;
		case 4:
			letters = "FDFCABABE";
			break;
		case 5:
			letters = "BECCECBFA";
			break;
		default:
			letters = "AAAAAAAAA";
		}
		for (int s = 0; s < 3; s++)
			for (int c = 0; c < 3; c++) {
				projectLetters[c + s * 3] = new Terminal();
				projectLetters[c + s * 3].color = colorList[c];
				projectLetters[c + s * 3].speed = s + 1;
				projectLetters[c + s * 3].letter = letters.charAt(c + s * 3);
			}
	}

	// GETTERS&SETTERS
	public char getLetter(Color color, int speed) {
		for (Terminal t : projectLetters)
			if (t.color == color && t.speed == speed)
				return t.letter;
		return ' ';
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

	class Terminal {
		public Color color;
		public int speed;
		public char letter;

		public Terminal() {
		}
	};

}
