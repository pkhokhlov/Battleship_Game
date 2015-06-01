package ships;

import java.awt.Color;

public class Destroyer extends Ship implements Colored
{
	public final Color color = Color.ORANGE;

	public Destroyer(String name, int size)
	{
		super(name, size);
	}
	
	public Color getColor()
	{
		return color;
	}
}
