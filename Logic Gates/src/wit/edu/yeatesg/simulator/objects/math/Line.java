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
	
	@Override
	public boolean intercepts(BigPoint p)
	{
		if (initial.x == terminal.x)
		{
			BigPoint top = initial;
			BigPoint bot = terminal;
			if (top.y > bot.y)
			{
				BigPoint temp = top;
				top = bot;
				bot = temp;
			}
			if (p.x == initial.x && p.y >= top.y && p.y <= bot.y)
			{
				return true;
			}
		}
		if (initial.y == terminal.y)
		{
			BigPoint left = initial;
			BigPoint right = terminal;
			if (left.x > right.x)
			{
				BigPoint temp = left;
				left = right;
				right = temp;
			}
			if (p.y == initial.y && p.x >= left.x && p.x <= right.x)
			{
				return true;
			}
		}
		return false;
	}
}
