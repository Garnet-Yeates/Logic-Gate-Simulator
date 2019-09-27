package wit.edu.yeatesg.simulator.objects.math;

public class LittlePoint
{	
	public int x;
	public int y;
	
	public LittlePoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void addOffset(Vector drawOffset)
	{
		this.x += drawOffset.x;
		this.y += drawOffset.y;
	}
	
	@Override
	public String toString()
	{
		return "( " + x + " , " + y + " )";
	}
}
