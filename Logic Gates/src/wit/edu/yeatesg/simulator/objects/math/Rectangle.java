package wit.edu.yeatesg.simulator.objects.math;

public class Rectangle
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

	public Rectangle copy()
	{
		return new Rectangle(topLeft, bottomRight);
	}
}
