package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalReceiver;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalSender;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;

public class Wire extends SignalEntity
{	
	private ArrayList<Wire> adjacentWires;
	
	private BigPoint startPoint;
	private BigPoint endPoint;
	
	private SignalEntity startConnection;
	private SignalEntity endConnection;
	
	private boolean horizontal;
		
	public Wire(BigPoint startPoint, BigPoint endPoint, Circuit circuit)
	{
		this(startPoint, endPoint, null, null, circuit);
		
	}
	
	public Wire(BigPoint startPoint, BigPoint endPoint, SignalEntity startConnection, SignalEntity endConnection, Circuit circuit)
	{
		assert startPoint.x == endPoint.x || startPoint.y == endPoint.y;
		horizontal = startPoint.x == endPoint.x ? false : true;
		
		this.circuit = circuit;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startConnection = startConnection;
		this.endConnection = endConnection;
		
		adjacentWires = new ArrayList<>();
		junctionPoints = new ArrayList<BigPoint>();
		
		circuit.addEntity(this);
		
		for (Wire intercepting : circuit.getAllWires())
		{	
			if (intercepting != this && (startPoint.interceptsWire(intercepting) || endPoint.interceptsWire(intercepting)))
			{
				BigPoint interceptingPoint = startPoint.interceptsWire(intercepting) ? startPoint : endPoint;
				
				if (intercepting.horizontal)
				{
					if (!interceptingPoint.equals(intercepting.startPoint) && !interceptingPoint.equals(intercepting.endPoint))
					{
						BigPoint oldLeftLoc = intercepting.getLeftPoint();
						
						if (intercepting.getRightPoint().equals(intercepting.getStartPoint()))
						{
							SignalEntity interceptingEndConnection = intercepting.getOutputConnection();		
							intercepting.endPoint = interceptingPoint;
							intercepting.endConnection = null;
							Wire leftWireSplice = new Wire(oldLeftLoc, interceptingPoint, circuit);
							leftWireSplice.startConnection = interceptingEndConnection;
						}
						else
						{
							SignalEntity interceptingStartConnection = intercepting.getInputConnection();
							intercepting.startPoint = interceptingPoint;
							intercepting.startConnection = null;
							Wire leftWireSplice = new Wire(oldLeftLoc, interceptingPoint, circuit);
							leftWireSplice.startConnection = interceptingStartConnection;		
						}
					}
				}
				else
				{
					if (!interceptingPoint.equals(intercepting.startPoint) && !interceptingPoint.equals(intercepting.endPoint))
					{
						BigPoint oldTopLoc = intercepting.getTopPoint();
						
						if (intercepting.getBottomPoint().equals(intercepting.getStartPoint()))
						{
							SignalEntity interceptingEndConnection = intercepting.getOutputConnection();		
							intercepting.endPoint = interceptingPoint;
							intercepting.endConnection = null;
							Wire topWireSplice = new Wire(oldTopLoc, interceptingPoint, circuit);
							topWireSplice.startConnection = interceptingEndConnection;
						}
						else
						{
							SignalEntity interceptingStartConnection = intercepting.getInputConnection();
							intercepting.startPoint = interceptingPoint;
							intercepting.startConnection = null;
							Wire topWireSplice = new Wire(oldTopLoc, interceptingPoint, circuit);
							topWireSplice.startConnection = interceptingStartConnection;
						}
					}
				}
				
			}
		}
		
		Wire.updateAllAdjacentWires(circuit);
	}
	
	public static void updateAllAdjacentWires(Circuit circuit)
	{	
		for (Wire w : circuit.getAllWires())
		{
			w.adjacentWires.clear();
			w.junctionPoints.clear();
		}
		
		for (Wire w : circuit.getAllWires())
		{
			for (Wire w2 : circuit.getAllWires())
			{
				if (w.intercepts(w2))
				{
					if (!w.adjacentWires.contains(w2))
					{
						w.adjacentWires.add(w2);
					}
					if (!w2.adjacentWires.contains(w))
					{
						w2.adjacentWires.add(w);
					}
				}
			}
		}
		
		for (Wire w : circuit.getAllWires())
		{
			HashMap<BigPoint, Integer> numInterceptions = new HashMap<>();
			for (Wire intercepting : w.adjacentWires)
			{
				BigPoint interceptingPoint = null;
				if (intercepting.startPoint.equals(w.startPoint) || intercepting.startPoint.equals(w.endPoint))
				{
					interceptingPoint = intercepting.startPoint;
				}
				if (intercepting.endPoint.equals(w.startPoint) || intercepting.endPoint.equals(w.endPoint))
				{
					interceptingPoint = intercepting.endPoint;
				}
				
				if (interceptingPoint != null)
				{
					if (numInterceptions.containsKey(interceptingPoint))
					{
						numInterceptions.put(interceptingPoint, numInterceptions.get(interceptingPoint) + 1);
					}
					else
					{
						numInterceptions.put(interceptingPoint, 0);
					}
				}
			}
			
			for (BigPoint p : numInterceptions.keySet())
			{
				if (numInterceptions.get(p) >= 2)
				{
					w.addJunctionPoint(p);
				}
			}
		}
		
		circuit.refreshTransmissions();
	}
	
	public boolean intercepts(Wire other)
	{
		if (startPoint.equals(other.endPoint) || startPoint.equals(other.startPoint) || endPoint.equals(other.startPoint) || endPoint.equals(other.endPoint))
		{
			return true;
		}
		return false;
	}
	
	private ArrayList<BigPoint> junctionPoints;
	
	private void addJunctionPoint(BigPoint p)
	{
		junctionPoints.add(p);
	}
	

	@SuppressWarnings("unchecked")
	public ArrayList<BigPoint> getJunctionLocations()
	{
		return (ArrayList<BigPoint>) junctionPoints.clone();
	}
	
	public void connect(Wire other)
	{
		other.adjacentWires.add(this);
		this.adjacentWires.add(other);
	}
	
	public BigPoint getStartPoint()
	{
		return startPoint.clone();
	}
	
	public BigPoint getEndPoint()
	{
		return endPoint.clone();
	}
	
	public BigPoint getLeftPoint()
	{
		if (horizontal)
		{
			return startPoint.x < endPoint.x ? startPoint : endPoint;
		}
		return null;
	}
	
	public BigPoint getRightPoint()
	{
		if (horizontal)
		{
			return getLeftPoint() == startPoint ? endPoint : startPoint;
		}
		return null;
	}
	
	public BigPoint getTopPoint()
	{
		if (!horizontal)
		{
			return startPoint.y < endPoint.y ? startPoint : endPoint;
		}
		return null;
	}
	
	public BigPoint getBottomPoint()
	{
		if (!horizontal)
		{
			return getTopPoint() == startPoint ? endPoint : startPoint;
		}
		return null;
	}
	
	public boolean isHorizontal()
	{
		return horizontal;
	}
	
	@Override
	public void transmit()
	{
		status = true;
		justUpdated = true;
		for (Wire adj : adjacentWires)
		{
			if (!adj.justUpdated)
			{
				adj.transmit();
			}
		}
		
		if (hasOutputConnection())
		{
			getOutputConnection().transmit();
		}
	}

	public boolean hasOutputConnection()
	{
		return startConnection instanceof SignalReceiver || endConnection instanceof SignalReceiver;
	}
	
	public boolean hasInputConnection()
	{
		return startConnection instanceof SignalSender || endConnection instanceof SignalSender;
	}
	
	public SignalReceiver getOutputConnection()
	{
		if (hasOutputConnection())
		{
			return startConnection instanceof SignalReceiver ? (SignalReceiver) startConnection : (SignalReceiver) endConnection;
		}
		return null;
	}
	
	public SignalSender getInputConnection()
	{
		if (hasInputConnection())
		{
			return startConnection instanceof SignalSender ? (SignalSender) startConnection : (SignalSender) endConnection;
		}
		return null;
	}
	
	public boolean getStatus()
	{
		return status;
	}

	@Override
	public void draw(Graphics g)
	{
		EditorPanel panel = circuit.getEditorPanel();
		GraphicsTools graphics = new GraphicsTools(panel, g);
		if (!horizontal) graphics.drawVerticalLine(startPoint, endPoint, g, 0);
		else graphics.drawHorizontalLine(startPoint, endPoint, g, 0);
		
		// TODO Auto-generated method stub
		
	}
}