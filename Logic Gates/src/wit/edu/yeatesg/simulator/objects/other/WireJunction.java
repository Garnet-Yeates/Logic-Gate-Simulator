package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import wit.edu.yeatesg.simulator.objects.abstractt.InterferingEntityException;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Shape;

public class WireJunction extends SignalEntity
{
	private ArrayList<Wire> connectedTo;
	
	public WireJunction(BigPoint location, Circuit c)
	{
		connectedTo = new ArrayList<>();
		this.circuit = c;
		this.location = location;
		
		c.addEntity(this);
		checkInterferingEntity();
	}
	
	public BigPoint getLocation()
	{
		return location.clone();
	}

	public boolean connectToWire(Wire w)
	{
		if (connectedTo.contains(w))
		{
			return false;
		}
		connectedTo.add(w);
		return true;
	}
	
	public static ArrayList<WireJunction> getAllWireJunctions(Circuit c)
	{
		return c.getAllWireJunctions();
	}
	
	public static void removeAllWireJunctions(Circuit c)
	{
		for (WireJunction j : c.getAllWireJunctions())
		{
			j.delete();
		}
	}

	@Override
	public void transmit()
	{
		if (!justUpdated)
		{
			justUpdated = true;
			status = true;
			for (Wire w : connectedTo)
			{
				w.transmit();
			}
		}		
	}

	@Override
	public void draw(Graphics g)
	{
		if (circuit.getGapBetweenPoints() > 5 && connectedTo.size() >= 3)
		{
			int width = (int) ((int) circuit.getGapBetweenPoints() / 1.6);
			int offset = width / 2;
			g.setColor(status ? Circuit.ON_COL : Circuit.OFF_COL);
			LittlePoint loc = LittlePoint.fromBigPoint(location, circuit);
			loc = LittlePoint.getPanelCoords(loc, circuit);
			g.fillOval(loc.x - offset, loc.y - offset, width, width);
		}
	}
	
	@Override
	public void drawSelectionIndicator(Graphics g)
	{
		for (Wire w : connectedTo)
		{
			w.drawSelectionIndicator(g);
		}
	}
	
	@Override
	public Shape getSelectionBounds()
	{
		return null; // A wire Junction's selection points are the union of all its wires selection points
	}
	
	@Override
	public void onDelete()
	{
		for (Wire w : connectedTo)
		{
			w.disconnectJunction(this);
		}
		connectedTo.clear();
	}

	public boolean disconnectWire(Wire w)
	{
		return connectedTo.remove(w);
	}
	
	public boolean equals(WireJunction other)
	{
		return other.location.equals(location);
	}

	@Override
	public boolean intercepts(BigPoint p)
	{
		return p.equals(location);
	}
	
	public ArrayList<Wire> getConnectedWires()
	{
		return connectedTo;
	}	
	
	public static boolean hasWireJunctionAt(BigPoint p, Circuit c)
	{
		return p.hasInterceptingWireJunction(c);
	}
	
	@Override
	public String toString()
	{
		String s = "Wire junction with the following wires: \n";
		for (Wire w : connectedTo)
		{
			s += w + "\n";
		}
		return s;
	}

	
	@Override
	public List<BigPoint> getDefaultPointSet()
	{
		return determine0PointSet();
	}

	@Override
	public List<BigPoint> determine0PointSet()
	{
		return new ArrayList<BigPoint>();
	}
	
	@Override
	public boolean isMovable()
	{
		return false;
	}

	@Override
	public boolean isPowerSource()
	{
		return false;
	}
}
