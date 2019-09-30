package wit.edu.yeatesg.simulator.objects.math;

import java.util.ArrayList;

public class Line extends Shape
{
	public BigPoint initial;
	public BigPoint terminal;
	
	public Line(BigPoint p1, BigPoint p2)
	{
		initial = p1;
		terminal = p2;
	}

	@Override
	public ArrayList<BigPoint> getAllPoints()
	{
		ArrayList<BigPoint> list = new ArrayList<BigPoint>();
		list.add(initial);
		list.add(terminal);
		return list;
	}
}
