package wit.edu.yeatesg.simulator.objects.other;

import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalSender;
import wit.edu.yeatesg.simulator.objects.math.Vector;

public class Circuit
{
	private ArrayList<Entity> entities;
	private EditorPanel panel;
	private Vector offset;
	private int gapBetweenPoints = 30;
	
	public Circuit(EditorPanel panel)
	{
		this.panel = panel;
		offset = new Vector(0, 0);
		entities = new ArrayList<Entity>();
	}
	
	public void refreshTransmissions()
	{
		resetSignalEntities();
		doTransmissions();		
	}
	
	public void resetSignalEntities()
	{
		for (Entity e : entities)
		{
			if (e instanceof SignalEntity)
			{
				((SignalEntity) e).setJustUpdated(false);
				((SignalEntity) e).setStatus(false);
			}
		}
	}
	
	public void doTransmissions()
	{
		for (SignalSender transmitter : getAllSignalSenders())
		{
			transmitter.transmit();
		}
	}
	
	public EditorPanel getEditorPanel()
	{
		return panel;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Entity> getEntities()
	{
		return (ArrayList<Entity>) entities.clone();
	}
	
	public void addEntity(Entity e)
	{
		entities.add(e);
	}
	
	public boolean canZoomIn()
	{
		return gapBetweenPoints < 70;
	}
	
	public boolean canZoomOut()
	{
		return gapBetweenPoints > 10; 
	}
	
	public boolean zoom(boolean in)
	{
		if (in && canZoomIn())
		{
			gapBetweenPoints += 10;
			return true;
		}
		
		if (!in && canZoomOut())
		{
			gapBetweenPoints -= 10;
			return true;
		}
		return false;
	}
	
	public int getGridPointDrawSize()
	{
		int r = gapBetweenPoints;
		int size = 1;
		size = (r >= 10) ? 3 : size;
		size = (r >= 20) ? 3 : size;
		size = (r >= 30) ? 3 : size;
		size = (r >= 40) ? 5 : size;
		size = (r >= 50) ? 5 : size;
		size = (r >= 60) ? 7 : size;
		return size;
	}
	
	public Vector getGridPointDrawOffset()
	{
		int size = getGridPointDrawSize();
		Vector offset = new Vector(0, 0);
		offset = (size == 1) ? new Vector(-0, -0) : offset; 
		offset = (size == 3) ? new Vector(-1, -1) : offset; 
		offset = (size == 5) ? new Vector(-2, -2) : offset; 
		offset = (size == 7) ? new Vector(-3, -3) : offset; 
		offset = (size == 9) ? new Vector(-4, -4) : offset; 
		offset = (size == 11) ? new Vector(-5, -5) : offset;
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
	
	public Vector getOffset()
	{
		return offset;
	}
	
	public int getGapBetweenPoints()
	{
		return gapBetweenPoints;
	}
	
	public ArrayList<Wire> getAllWires()
	{
		ArrayList<Wire> wireList = new ArrayList<>();
		for (Entity e : getEntities())
		{
			if (e instanceof Wire)
			{
				wireList.add((Wire) e);
			}
		}
		return wireList;
	}
	
	public ArrayList<SignalSender> getAllSignalSenders()
	{
		ArrayList<SignalSender> senderList = new ArrayList<>();
		for (Entity e : getEntities())
		{
			if (e instanceof SignalSender)
			{
				senderList.add((SignalSender) e);
			}
		}
		return senderList;
	} 
}
