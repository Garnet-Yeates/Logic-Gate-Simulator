package wit.edu.yeatesg.simulator.objects.math;

import wit.edu.yeatesg.simulator.objects.other.Wire;

public class BigPoint
{	
	public double x;
	public double y;
	
	public BigPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString()
	{
		return "( " + x + " , " + y + " )";
	}
	
	public BigPoint clone()
	{
		return new BigPoint(x, y);
	}
	
	public boolean equals(BigPoint other)
	{
		return other.x == x && other.y == y;
	}
	
	public boolean interceptsWire(Wire w)
	{
		if (w.isHorizontal())
		{
			if (y == w.getLeftPoint().y && x >= w.getLeftPoint().x && x <= w.getRightPoint().x)
			{
				return true;
			}
		}
		else
		{
			if (x == w.
					getTopPoint().x && y >= w.
					getTopPoint().y && y <= w.
					getBottomPoint().y)
			{
				return true;
			}
		}
		return false;
	}
}
