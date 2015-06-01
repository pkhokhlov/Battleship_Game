package ships;

import java.awt.Color;

public class PatrolBoat extends Ship implements Colored
{
	public final Color color = Color.GREEN;
	
	public PatrolBoat(String name, int size)
	{
		super(name, size);
	}
	
	public Color getColor()
	{
		return color;
	}
}
