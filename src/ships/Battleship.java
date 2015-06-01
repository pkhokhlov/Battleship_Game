package ships;

import java.awt.Color;

public class Battleship extends Ship implements Colored
{
	public final Color color = Color.GRAY;
	
	public Battleship(String name, int size)
	{
		super(name, size);
	}
	
	public Color getColor()
	{
		return color;
	}
}
