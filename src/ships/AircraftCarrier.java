package ships;

import java.awt.Color;

public class AircraftCarrier extends Ship implements Colored
{
	public final Color color = Color.BLACK;

	public AircraftCarrier(String name, int size)
	{
		super(name, size);
	}
	
	public Color getColor()
	{
		return color;
	}

}
