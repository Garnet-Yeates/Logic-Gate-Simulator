package wit.edu.yeatesg.simulator.objects.math;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
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

	// Prioritizes Junctions then Wires then ConnectionNodes then regular Entities
	public ArrayList<Entity> getInterceptingEntities(Circuit c)
	{
		ArrayList<Entity> intercepting = new ArrayList<Entity>();

		ArrayList<Entity> one = new ArrayList<>();
		ArrayList<Entity> two = new ArrayList<>();
		ArrayList<Entity> three = new ArrayList<>();
		ArrayList<Entity> four = new ArrayList<>();

		for (Entity e : c.getAllEntities())
		{
			if (e.intercepts(this))
			{
				switch (e.getClass().getSimpleName())
				{
				case "WireJunction":
					one.add(e);
					break;
				case "Wire":
					two.add(e);
					break;
				case "ConnectionNode":
					three.add(e);
					break;
				case "Entity":
					four.add(e);
					break;
				}
			}
		}
		
		three.addAll(four);
		two.addAll(three);
		one.addAll(two);
		intercepting.addAll(one);
		
		return intercepting;
	}

	public boolean hasInterceptingEntity(Circuit c)
	{
		return getInterceptingEntities(c).size() > 0;
	}

	public Entity getInterceptingEntity(Circuit c)
	{
		return getInterceptingEntities(c).get(0);
	}

	public boolean hasInterceptingConnectionNode(Circuit c)
	{
		for (Entity e : getInterceptingEntities(c))
			if (e instanceof ConnectionNode)
				return true;
		return false;
	}

	public boolean hasInterceptingWire(Circuit c)
	{
		for (Entity e : getInterceptingEntities(c))
			if (e instanceof Wire)
				return true;
		return false;
	}

	public boolean hasInterceptingWireJunction(Circuit c)
	{
		for (Entity e : getInterceptingEntities(c))
			if (e instanceof WireJunction)
				return true;
		return false;
	}

	public WireJunction getInterceptingWireJunction(Circuit c)
	{
		if (hasInterceptingWireJunction(c))
			for (Entity e : getInterceptingEntities(c))
				if (e instanceof WireJunction)
					return (WireJunction) e;
		return null;
	}

	public Wire getInterceptingWire(Circuit c)
	{
		if (hasInterceptingWire(c))
			for (Entity e : getInterceptingEntities(c))
				if (e instanceof Wire)
					return (Wire) e;
		return null;
	}

	public ConnectionNode getInterceptingConnectionNode(Circuit c)
	{
		if (hasInterceptingConnectionNode(c))
			for (Entity e : getInterceptingEntities(c))
				if (e instanceof ConnectionNode)
					return (ConnectionNode) e;
		return null;
	}

	public Entity getInterceptingHitboxEntity(Circuit c)
	{
		for (Entity e : c.getAllEntities())
			if (e.getSelectionBounds() != null && e.getSelectionBounds().intercepts(this) && !(e instanceof Wire))
				return e;
		return null;
	}
	
	public boolean hasInterceptingHitBoxEntity(Circuit c)
	{
		return getInterceptingHitboxEntity(c) != null;
	}

	public boolean hasInterceptingConnectable(Circuit c)
	{
		return hasInterceptingConnectionNode(c) || hasInterceptingWireJunction(c) || hasInterceptingWire(c);
	}

	public SignalEntity getInterceptingConnectable(Circuit c)
	{
		if (hasInterceptingConnectable(c))
			for (Entity e : getInterceptingEntities(c))
				if (e instanceof WireJunction || e instanceof Wire || e instanceof ConnectionNode)
					return (SignalEntity) e;
		return null;	
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

	public BigPoint addVector(Vector v)
	{
		return new BigPoint(x + v.x, y + v.y);
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

	public boolean isAdjacentTo(BigPoint other)
	{
		for (BigPoint p : getAdjacentPoints())
		{
			if (p.equals(other))
			{
				return true;
			}
		}
		return false;
	}

	public ArrayList<BigPoint> getAdjacentPoints()
	{
		ArrayList<BigPoint> adjList = new ArrayList<>();
		adjList.add(new BigPoint(x - 1, y));
		adjList.add(new BigPoint(x + 1, y));
		adjList.add(new BigPoint(x, y - 1));
		adjList.add(new BigPoint(x, y + 1));
		return adjList;
	}
}
