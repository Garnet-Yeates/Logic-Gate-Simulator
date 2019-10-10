package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Graphics;
import java.util.List;

import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Shape;

public class ConnectionNode extends SignalEntity
{
	private boolean input;
	private Wire connected;
	
	public ConnectionNode(BigPoint loc, Circuit c)
	{
		location = loc;
		circuit = c;
		checkInterferingEntity();
		circuit.addEntity(this);
	}
	
	@Override
	public void transmit()
	{
		if (!justUpdated)
		{
			status = true;
			justUpdated = true;
			if (connected != null) connected.transmit();
		}
	}
	
	@Override
	public void draw(Graphics g)
	{
		LittlePoint drawPoint = LittlePoint.getPanelCoords(location, circuit);
		int offset = -1*circuit.getGapBetweenPoints() / 4;
		int length = -1*offset * 2;
		g.setColor(status ? Circuit.ON_COL : Circuit.OFF_COL);
		g.fillOval(drawPoint.x + offset, drawPoint.y + offset, length, length);
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean intercepts(BigPoint p)
	{
		return p.equals(location);
	}

	@Override
	public void onDelete()
	{
		// TODO Auto-generated method stub	
	}
	
	public void connect(Wire w)
	{
		connected = w;
	}
	
	public Wire getConnectedWire()
	{
		return connected;
	}
	
	public void disconnectWire()
	{
		connected = null;
	}

	@Override
	public Shape getSelectionBounds()
	{
		return null;
	}
	
	public boolean isInputConnection()
	{
		return input;
	}
	
	public boolean isOutputConnection()
	{
		return !input;
	}

	@Override
	public List<BigPoint> getDefaultPointSet()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BigPoint> determine0PointSet()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isMovable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPowerSource()
	{
		return false;
	}
}
