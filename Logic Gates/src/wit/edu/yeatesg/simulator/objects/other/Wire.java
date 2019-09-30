package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Graphics;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.HashMap;

import wit.edu.yeatesg.simulator.objects.abstractt.InterferingEntityException;
import wit.edu.yeatesg.simulator.objects.abstractt.InvalidWireException;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalReceiver;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalSender;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.Line;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Shape;
import wit.edu.yeatesg.simulator.objects.math.Vector;

public class Wire extends SignalEntity
{		
	private BigPoint startPoint;
	private BigPoint endPoint;
	
	private SignalEntity startConnection;
	private SignalEntity endConnection;
	
	private ArrayList<WireJunction> connectedJunctions;
	
	private boolean horizontal;
		
	public Wire(BigPoint startPoint, BigPoint endPoint, Circuit circuit)
	{
		this(startPoint, endPoint, null, null, circuit);
	}
		
	public Wire(BigPoint startPoint, BigPoint endPoint, SignalEntity startConnection, SignalEntity endConnection, Circuit circuit)
	{		
		horizontal = startPoint.x == endPoint.x ? false : true;
		
		this.circuit = circuit;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startConnection = startConnection;
		this.endConnection = endConnection;
		
		checkInvalidWire();
		
		connectedJunctions = new ArrayList<>();
		
		circuit.addEntity(this);
		wireBisectCheck(circuit);
		
		Wire.updateWires(circuit);
	}
	
	public static void wireBisectCheck(Wire w, Circuit circuit)
	{
		for (Wire intercepting : circuit.getAllWires())
		{	
			if (intercepting != w && (w.startPoint.intercepts(intercepting) || w.endPoint.intercepts(intercepting)))
			{
				BigPoint interceptingPoint = w.startPoint.intercepts(intercepting) ? w.startPoint : w.endPoint;
				
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
	}
	
	public static void wireBisectCheck(Circuit c)
	{
		for (Wire w : c.getAllWires())
		{
			Wire.wireBisectCheck(w, c);
		}
	}
	
	public void checkInvalidWire()
	{
		if (!((startPoint.x == endPoint.x && startPoint.y != endPoint.y) || (startPoint.y == endPoint.y && startPoint.x != endPoint.x)))
		{
			throw new InvalidWireException();
		}
		
		for (Wire w : Wire.wiresThatHaveAnEdgePointAt(startPoint, circuit))
		{
			if (Wire.wiresThatHaveAnEdgePointAt(endPoint, circuit).contains(w))
			{
				throw new InterferingEntityException();
			}
		}
	}
	
	public static void updateWires(Circuit circuit)
	{	
		WireJunction.removeAllWireJunctions(circuit);
		
		for (Wire w : circuit.getAllWires())
		{
			w.connectedJunctions.clear();
		}
		
		for (Wire w : circuit.getAllWires())
		{
			ArrayList<Wire> startPointInterceptions = Wire.wiresThatHaveAnEdgePointAt(w.startPoint, w.circuit);
			ArrayList<Wire> endPointInterceptions = Wire.wiresThatHaveAnEdgePointAt(w.endPoint, w.circuit);
			BigPoint interceptPoint = null;
			if (startPointInterceptions.size() >= 2)
			{
				interceptPoint = w.startPoint.clone();
				for (Wire wireToConnect : startPointInterceptions)
				{
					if (!wireToConnect.hasJunctionAt(interceptPoint))
					{
						wireToConnect.connectJunction(interceptPoint);
					}
				}
			}
			if (endPointInterceptions.size() >= 2)
			{
				interceptPoint = w.endPoint.clone();
				for (Wire wireToConnect : endPointInterceptions)
				{
					if (!wireToConnect.hasJunctionAt(interceptPoint))
					{
						wireToConnect.connectJunction(interceptPoint);
					}
				}
			}	
		}
		circuit.refreshTransmissions();
	}
	
	public void connectJunction(BigPoint p)
	{
		if (p.hasInterceptingWireJunction(circuit))
		{
			WireJunction junc = (WireJunction) p.getInterceptingEntity(circuit);
			junc.connectToWire(this);
			connectedJunctions.add(junc);
		}
		else
		{
			WireJunction junc = new WireJunction(p, circuit);
			junc.connectToWire(this);
			connectedJunctions.add(junc);
		}
	}
	
	public boolean disconnectJunction(WireJunction wireJunction)
	{
		if (connectedJunctions.contains(wireJunction))
		{
			connectedJunctions.remove(wireJunction);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<WireJunction> getConnectedJunctions()
	{
		return (ArrayList<WireJunction>) connectedJunctions.clone();
	}
	
	@Override
	public Shape getSelectionBounds()
	{
		return new Line(startPoint, endPoint);
	}
	
	public boolean intercepts(Wire other)
	{
		if (startPoint.equals(other.endPoint) || startPoint.equals(other.startPoint) || endPoint.equals(other.startPoint) || endPoint.equals(other.endPoint))
		{
			return true;
		}
		return false;
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
		if (!justUpdated)
		{
			status = true;
			justUpdated = true;
			for (WireJunction j : connectedJunctions)
			{
				j.transmit();
			}
			
			if (hasOutputConnection())
			{
				getOutputConnection().transmit();
			}
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

	public static final Color ON_COL = new Color(0, 255, 0);
	public static final Color OFF_COL = new Color(0, 140, 0);
	
	@Override
	public void draw(Graphics g)
	{
		g.setColor(status ? ON_COL : OFF_COL);
		int extraThicc = circuit.getGapBetweenPoints() / 9;
		if (!horizontal) GraphicsTools.drawVerticalLine(startPoint, endPoint, extraThicc + circuit.getGridPointDrawOffset(), g, circuit);
		else GraphicsTools.drawHorizontalLine(startPoint, endPoint, extraThicc + circuit.getGridPointDrawOffset(), g, circuit);
		// TODO Auto-generated method stub	
	}
	
	public static ArrayList<Wire> wiresThatHaveAnEdgePointAt(BigPoint p, Circuit circuit)
	{
		ArrayList<Wire> list = new ArrayList<>();
		for (Wire w : circuit.getAllWires())
		{
			if (w.getStartPoint().equals(p) || w.getEndPoint().equals(p))
			{
				list.add(w);
			}
		}
		return list;
	}
	
	public boolean hasJunctionAt(BigPoint p)
	{
		for (WireJunction junc : connectedJunctions)
		{
			if (junc.getLocation().equals(p))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		Vector dir = new Vector(startPoint, endPoint);
		String direction = horizontal ? dir.x > 0 ? "right" : "left" : dir.y > 0  ? "down" : "up";
		return startPoint + " -> " + (int) dir.getLength() + " units " + direction + " -> " + endPoint;
	}

	@Override
	public void onDelete()
	{
		if (hasInputConnection())
		{
			getInputConnection().disconnectWire();
		}
		
		if (hasOutputConnection())
		{
			getOutputConnection().disconnectWire();
		}
	}

	@Override
	public boolean intercepts(BigPoint p)
	{
		if (isHorizontal())
		{
			if (p.y == getLeftPoint().y && p.x >= getLeftPoint().x && p.x <= getRightPoint().x)
			{
				return true;
			}
		}
		else
		{
			if (p.x == getTopPoint().x && p.y >= getTopPoint().y && p.y <= getBottomPoint().y)
			{
				return true;
			}
		}
		return false;
	}

	public void setStartPoint(BigPoint p)
	{
		startPoint = p;
	}
	
	public void setEndPoint(BigPoint p)
	{
		endPoint = p;
	}
	
	@Override
	public boolean withinDrawingBounds()
	{
		return withinDrawingBounds(endPoint, circuit) || withinDrawingBounds(startPoint, circuit);
	}	
}