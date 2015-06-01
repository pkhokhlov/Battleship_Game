package battleship_game;

import java.awt.Color;

import javax.swing.JButton;

public class playerButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3777233735681115079L;
	public int xpos_;
	public int ypos_;
	
	public Color prevColor;
	
	public playerButton(int x, int y)
	{
		super();
		xpos_ = x;
		ypos_ = y;
		prevColor = null;
	}
}
