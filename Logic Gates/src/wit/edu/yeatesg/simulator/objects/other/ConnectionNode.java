package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Graphics;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.Shape;

public class ConnectionNode extends Entity
{

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean intercepts(BigPoint p)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDelete()
	{
		// TODO Auto-generated method stub	
	}
	
	public void connect(Wire w)
	{
		
	}

	@Override
	public Shape getSelectionBounds()
	{
		return null;
	}
	
	@Override
	public void drawSelectionIndicator(Graphics g)
	{
		// Dont do shit, rlly
	}

}
