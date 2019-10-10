package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Color;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.Vector;

public class Circuit
{
	public static final Color ON_COL = new Color(0, 255, 0);
	public static final Color OFF_COL = new Color(0, 170, 0);
	
	private ArrayList<Entity> entities;
	private EditorPanel panel;
	private Vector offset;
	private int gapBetweenPoints = 20;
	
	private int width;
	private int height;
	
	public Circuit(EditorPanel panel)
	{
		this.panel = panel;
		offset = new Vector(0, 0);
		entities = new ArrayList<Entity>();
	}
	
	public void resetSignalEntities()
	{
		for (Entity e : entities)
		{
			if (e instanceof SignalEntity)
			{
				((SignalEntity) e).setJustUpdated(false);
				
				if (!((SignalEntity) e).isPowerSource()) // Power sources have their on way of being toggled
					((SignalEntity) e).setStatus(false);
			}
		}
	}
	
	public void refreshTransmissions()
	{
		resetSignalEntities();
		for (Entity e : entities)
		{
			if (e instanceof SignalEntity && ((SignalEntity) e).isPowerSource())
			{
				((SignalEntity) e).transmit();
			}
		}
	}
	
	public EditorPanel getEditorPanel()
	{
		return panel;
	}

	public void addEntity(Entity e)
	{
		entities.add(e);
		panel.repaint();
	}
		
	public boolean removeEntity(Entity e)
	{
		boolean b = entities.remove(e);
		System.out.println("Removed " + e + "? " + b);
		return b;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Entity> getAllEntities()
	{
		return (ArrayList<Entity>) entities.clone();
	}

	public ArrayList<WireJunction> getAllWireJunctions()
	{
		ArrayList<WireJunction> junctionList = new ArrayList<>();
		for (Entity e : getAllEntities())
		{
			if (e instanceof WireJunction)
			{
				junctionList.add((WireJunction) e);
			}
		}
		return junctionList;
	}
	
	public ArrayList<ConnectionNode> getAllConnectionNodes()
	{
		ArrayList<ConnectionNode> nodeList = new ArrayList<>();
		for (Entity e : getAllEntities())
		{
			if (e instanceof ConnectionNode)
			{
				nodeList.add((ConnectionNode) e);
			}
		}
		return nodeList;
	}
	
	public ArrayList<Wire> getAllWires()
	{
		ArrayList<Wire> wireList = new ArrayList<>();
		for (Entity e : getAllEntities())
		{
			if (e instanceof Wire)
			{
				wireList.add((Wire) e);
			}
		}
		return wireList;
	}
	
	public int getGridPointDrawSize()
	{
		int r = gapBetweenPoints;
		int size = 1;
		size = (r >= 5) ? 1 : size;
		size = (r >= 10) ? 1 : size;
		size = (r >= 15) ? 1 : size;
		size = (r >= 20) ? 2 : size;
		size = (r >= 25) ? 2 : size;
		size = (r >= 30) ? 2 : size;
		size = (r >= 35) ? 3 : size;
		size = (r >= 40) ? 3 : size;
		return size;
	}
	
	public int getGridPointDrawOffset()
	{
		int size = getGridPointDrawSize();
		int offset = 0;
		offset = (size == 1) ? 0 : offset; 
		offset = (size == 3) ? -1 : offset; 
		offset = (size == 2) ? 0 : offset; 
		offset = (size == 7) ? -3 : offset; 
		offset = (size == 9) ? -4 : offset; 
		offset = (size == 11) ? -5 : offset;
		return offset;
	}
	
	public boolean canZoomIn()
	{
		return gapBetweenPoints < 40;
	}
	
	public boolean canZoomOut()
	{
		return gapBetweenPoints > 5; 
	}
	
	public boolean zoom(boolean in)
	{
		if (in && canZoomIn())
		{
			gapBetweenPoints += 5;
			return true;
		}
		
		if (!in && canZoomOut())
		{
			gapBetweenPoints -= 5;
			return true;
		}
		return false;
	}
	
	public Vector getOffset()
	{
		return offset;
	}
	
	public void modifyOffset(Vector v)
	{
		offset = offset.add(v);
	}
	
	public void resetOffset()
	{
		offset.x = 0;
		offset.y = 0;
	}
	
	public int getGapBetweenPoints()
	{
		return gapBetweenPoints;
	}
}