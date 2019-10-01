package wit.edu.yeatesg.simulator.objects.math;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.other.Circuit;
import wit.edu.yeatesg.simulator.objects.other.ConnectionNode;
import wit.edu.yeatesg.simulator.objects.other.Wire;
import wit.edu.yeatesg.simulator.objects.other.WireJunction;

public class BigPoint
{	
	public double x;
	public double y;
	
	public BigPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean hasInterceptingEntity(Circuit c)
	{
		return getInterceptingEntity(c) != null;
	}
	
	// Prioritizes Wires then Junctions then regular selection nodes
	public Entity getInterceptingEntity(Circuit c)
	{
		for (WireJunction junc : c.getAllWireJunctions())
		{
			if (junc.intercepts(this))
			{
				return junc;
			}
		}
		
		for (Wire w : c.getAllWires())
		{
			if (w.intercepts(this))
			{
				return w;
			}
		}
		
		for (Entity e : c.getAllEntities())
		{
			if (e.intercepts(this))
			{
				return e;
			}
		}
		return null;
	}
	
	public boolean hasInterceptingConnectionNode(Circuit c)
	{
		return getInterceptingEntity(c) instanceof ConnectionNode;
	}
	
	public boolean hasInterceptingWire(Circuit c)
	{
		return getInterceptingEntity(c) instanceof Wire;
	}
	
	public boolean hasInterceptingWireJunction(Circuit c)
	{
		return getInterceptingEntity(c) instanceof WireJunction;
	}
	
	public boolean hasInterceptingConnectable(Circuit c)
	{
		 return hasInterceptingConnectionNode(c) || hasInterceptingWireJunction(c) || hasInterceptingWire(c);
	}
	
	public boolean intercepts(Entity e)
	{
		return e.intercepts(this);
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
	
	public static BigPoint fromLittlePoint(LittlePoint editorCoords, Circuit circuit)
	{
		return new BigPoint((double) editorCoords.x / (double) circuit.getGapBetweenPoints(), (double) editorCoords.y / (double) circuit.getGapBetweenPoints());
	}
	
	public static BigPoint fromMouseEvent(MouseEvent e, Circuit circuit)
	{
		return BigPoint.fromLittlePoint(LittlePoint.getEditorCoords(e, circuit), circuit);
	}
	
	public static BigPoint fromPanelCoords(LittlePoint panelCoords, Circuit circuit)
	{
		return BigPoint.fromLittlePoint(LittlePoint.getEditorCoords(panelCoords, circuit), circuit);
	}

	public static BigPoint closestTo(LittlePoint editorCoords, Circuit circuit)
	{
		BigPoint loc = BigPoint.fromLittlePoint(editorCoords, circuit);
		loc = new BigPoint(Math.round(loc.x), Math.round(loc.y));
		return loc;
	}
	
	public static ArrayList<BigPoint> getPointsBetween(BigPoint p1, BigPoint p2)
	{
		ArrayList<BigPoint> list = new ArrayList<>();
		if (p1.x == p2.x)
		{
			int x = (int) p1.x;
			if (p1.y > p2.y)
			{
				BigPoint temp = p1;
				p1 = p2;
				p2 = temp;
			}
			for (int y = (int) (p1.y + 1); y < p2.y; y++) list.add(new BigPoint(x, y));
		}
		if (p1.y == p2.y)
		{
			int y = (int) p1.y;
			if (p1.x > p2.x)
			{
				BigPoint temp = p1;
				p1 = p2;
				p2 = temp;
			}
			for (int x = (int) (p1.x + 1); x < p2.x; x++) list.add(new BigPoint(x, y));
		}
		return list;
	}
	
	public void draw(Graphics g, Circuit c)
	{
		LittlePoint drawPoint = LittlePoint.getPanelCoords((LittlePoint.fromBigPoint(this, c)), c);
		drawPoint.addOffset(c.getGridPointDrawOffset());
		g.fillRect((int)drawPoint.x, (int)drawPoint.y, c.getGridPointDrawSize(), c.getGridPointDrawSize());
	}

	public BigPoint round()
	{
		return new BigPoint(Math.round(x), Math.round(y));
	}
}
