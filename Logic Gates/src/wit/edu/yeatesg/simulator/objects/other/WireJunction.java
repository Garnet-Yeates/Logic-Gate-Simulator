package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Graphics;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.abstractt.InterferingEntityException;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;

public class WireJunction extends SignalEntity
{
	private BigPoint location;
	private ArrayList<Wire> connectedTo;
	
	public WireJunction(BigPoint location, Circuit c)
	{
		System.out.println("NEW WIRE JUNCTION NIBBA");
		connectedTo = new ArrayList<>();
		this.circuit = c;
		this.location = location;

		for (WireJunction j : WireJunction.getAllWireJunctions(circuit))
		{
			if (j != this && j.location.equals(location))
			{
				throw new InterferingEntityException();
			}
		}
		
		c.addEntity(this);

	}
	
	public BigPoint getLocation()
	{
		return location.clone();
	}
	
	public static boolean hasJunctionAt(BigPoint p, Circuit c)
	{
		return getJunctionAt(p, c) != null;
	}
	
	public static WireJunction getJunctionAt(BigPoint p, Circuit c)
	{
		for (WireJunction j : c.getAllWireJunctions())
		{
			if (j.getLocation().equals(p))
			{
				return j;
			}
		}
		return null;
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
			System.out.println("Dekete");
			j.delete();
		}
	}

	@Override
	public void transmit()
	{
		if (!justUpdated)
		{
			for (Wire w : connectedTo)
			{
				w.transmit();
			}
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
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
	
}
