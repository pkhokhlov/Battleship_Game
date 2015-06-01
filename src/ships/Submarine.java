package ships;

import java.awt.Color;

public class Submarine extends Ship implements Colored
{
	public final Color color = Color.BLUE;
	
	public Submarine(String name, int size)
	{
		super(name, size);
	}

	public Color getColor()
	{
		return color;
	}
	
	
}
