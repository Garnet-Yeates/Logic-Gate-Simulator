package wit.edu.yeatesg.simulator.objects.math;

public class Vector
{
	public int x;
	public int y;
	
	public Vector(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector(LittlePoint initial, LittlePoint terminal)
	{
		initial = initial == null ? new LittlePoint(0, 0) : initial;
		terminal = terminal == null ? new LittlePoint(0, 0) : terminal;
		x = (int) (terminal.x - initial.x);
		y = (int) (terminal.y - initial.y);
	}

	public Vector multiply(int t)
	{
		return new Vector(x * t, y * t);
	}
	
	@Override
	public String toString()
	{
		return "< " + x + " , " + y + " >";
	}

	public Vector add(Vector v)
	{
		return new Vector(v.x + x, v.y + y);
	}
}
