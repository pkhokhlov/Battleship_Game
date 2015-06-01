package ships;

public class Ship implements Colored
{
	public String name_;
	public int size_;
	
	public Ship(String name, int size)
	{
		name_ = name;
		size_ = size;
	}

	public String getName()
	{
		return name_;
	}
	
	public int getSize()
	{
		return size_;
	}
}
