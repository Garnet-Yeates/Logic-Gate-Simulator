package wit.edu.yeatesg.simulator.objects.math;

import java.util.ArrayList;

public class Rectangle extends Shape
{
	public BigPoint topLeft, topRight, bottomLeft, bottomRight;

	public Rectangle(BigPoint topLeft, BigPoint topRight, BigPoint bottomLeft, BigPoint bottomRight)
	{
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		
	}
	
	public Rectangle(BigPoint topLeft, BigPoint bottomRight)
	{
		this(topLeft, new BigPoint(bottomRight.x, topLeft.y), new BigPoint(topLeft.x, bottomRight.y), bottomRight);
	}
	
	@Override
	public ArrayList<BigPoint> getAllPoints()
	{
		ArrayList<BigPoint> list = new ArrayList<BigPoint>();
		list.add(topLeft);
		list.add(topRight);
		list.add(bottomLeft);
		list.add(bottomRight);
		return list;
	}

	public Rectangle copy()
	{
		return new Rectangle(topLeft, bottomRight);
	}
}
