package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Graphics;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.HashMap;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
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
	
	private static int idAssign = 0;
	public int id;
	
	private ConnectionNode startConnection;
	private ConnectionNode endConnection;
	
	private ArrayList<WireJunction> connectedJunctions;
	
	private boolean horizontal;
		
	public Wire(BigPoint startPoint, BigPoint endPoint, Circuit circuit)
	{
		this(startPoint, endPoint, null, null, circuit);
	}
		
	public Wire(BigPoint startPoint, BigPoint endPoint, ConnectionNode startConnection, ConnectionNode endConnection, Circuit circuit)
	{		
		id = idAssign++;
		System.out.println("NEW WIRE");
		horizontal = startPoint.x == endPoint.x ? false : true;
		
		this.circuit = circuit;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startConnection = startConnection;
		this.endConnection = endConnection;
		
		checkInvalidWire();
		
		connectedJunctions = new ArrayList<>();
		
		circuit.addEntity(this);
		wireBisectAndConnectionCheck(circuit);
		
		Wire.updateWires(circuit);
	}
	
	public static void wireBisectAndConnectionCheck(Circuit c)
	{
		for (Entity e : c.getAllEntities())
		{
			if (e instanceof Wire)
			{
				Wire w = (Wire) e;
				
				BigPoint startPoint = w.getStartPoint();
				BigPoint endPoint = w.getEndPoint();
				
				if (w.startConnection != null && !w.startConnection.getLocation().equals(startPoint))
				{
					w.startConnection.disconnectWire();
					w.startConnection = null;
				}
				
				if (w.endConnection != null && !w.endConnection.getLocation().equals(endPoint))
				{
					w.endConnection.disconnectWire();
					w.endConnection = null;
				}
				
				for (Entity e2 : c.getAllWires())
				{	
					if (e2 instanceof Wire)
					{
						Wire intercepting = (Wire) e2;
						
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
										ConnectionNode interceptingEndConnection = intercepting.getOutputConnection();		
										intercepting.endPoint = interceptingPoint;
										intercepting.endConnection = null;
										Wire leftWireSplice = new Wire(oldLeftLoc, interceptingPoint, c);
										leftWireSplice.startConnection = interceptingEndConnection;
									}
									else
									{
										ConnectionNode interceptingStartConnection = intercepting.getInputConnection();
										intercepting.startPoint = interceptingPoint;
										intercepting.startConnection = null;
										Wire leftWireSplice = new Wire(oldLeftLoc, interceptingPoint, c);
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
										ConnectionNode interceptingEndConnection = intercepting.getOutputConnection();		
										intercepting.endPoint = interceptingPoint;
										intercepting.endConnection = null;
										Wire topWireSplice = new Wire(oldTopLoc, interceptingPoint, c);
										topWireSplice.startConnection = interceptingEndConnection;
									}
									else
									{
										ConnectionNode interceptingStartConnection = intercepting.getInputConnection();
										intercepting.startPoint = interceptingPoint;
										intercepting.startConnection = null;
										Wire topWireSplice = new Wire(oldTopLoc, interceptingPoint, c);
										topWireSplice.startConnection = interceptingStartConnection;
									}
								}
							}
						}
					}
					else if (e2 instanceof ConnectionNode)
					{
						ConnectionNode aNode = (ConnectionNode) e2;
						if (aNode.getLocation().equals(w.startPoint))
						{
							w.startConnection = aNode;
							aNode.connect(w);
						}
						if (aNode.getLocation().equals(w.endPoint))
						{
							w.endConnection = aNode;
							aNode.connect(w);
						}
					}	
				}	
			}
			else if (e instanceof ConnectionNode)
			{
				ConnectionNode node = (ConnectionNode) e;
				Wire w = node.getConnectedWire();
				if (w.startConnection == node || w.endConnection == node)
				{
					if (w.startConnection == node)
					{
						if (!node.getLocation().equals(w.startPoint))
						{
							node.disconnectWire();
							w.startConnection = null;
						}
					}
					if (w.endConnection == node)
					{
						if (!node.getLocation().equals(w.endPoint))
						{
							node.disconnectWire();
							w.endConnection = null;
						}
					}
				}
				else
				{
					node.disconnectWire();
				}
			}
		}
		
		updateWires(c);
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
		
		outer :for (WireJunction junc : circuit.getAllWireJunctions())
		{
			if (junc.getConnectedWires().size() > 0)
			{
				boolean horizontal = junc.getConnectedWires().get(0).horizontal;
				for (Wire w : junc.getConnectedWires())
				{
					if (w.horizontal != horizontal)
					{
						continue outer;
					}
				}
				System.out.println(junc.getConnectedWires().size());
				// If it gets to this point, there are 2 wires on the junction and both are going in the same direction (so they should be merged into one wire)
				Wire w1 = junc.getConnectedWires().get(0);
				Wire w2 = junc.getConnectedWires().get(1);
				
				BigPoint interceptLoc = junc.getLocation();
				boolean movingW1Start = false;
				
				movingW1Start = interceptLoc.equals(w1.startPoint) ? true : false;
				
				if (interceptLoc.equals(w2.endPoint))
				{
					BigPoint w2Start = w2.getStartPoint();
					if (movingW1Start)
					{
						System.out.println("MOVED W1 ( " + w1 + " )" + "START POINT TO W2 ( " + w2 + " ) START POINT");
						w2.delete();
						w1.setStartPoint(w2Start);
					}
					else
					{
						System.out.println("MOVED W1 ( " + w1 + " )" + "END POINT TO W2 ( " + w2 + " ) START POINT");
						w2.delete();
						w1.setEndPoint(w2Start);
					}
				}
				else
				{
					BigPoint w2End = w2.getEndPoint();
					if (movingW1Start)
					{
						System.out.println("MOVED W1 ( " + w1 + " )" + "START POINT TO W2 ( " + w2 + " ) END POINT");
						w2.delete();
						w1.setStartPoint(w2End);
					}
					else
					{
						System.out.println("MOVED W1 ( " + w1 + " )" + "END POINT TO W2 ( " + w2 + " ) END POINT");
						w2.delete();
						w1.setEndPoint(w2End);
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
		return getOutputConnection() != null;
	}
	
	public boolean hasInputConnection()
	{
		return getInputConnection() != null;
	}
	
	public ConnectionNode getOutputConnection()
	{
		if (startConnection != null && startConnection.isOutputConnection())
		{
			return startConnection;
		}
		if (endConnection != null && endConnection.isOutputConnection())
		{
			return endConnection;
		}
		return null;
	}
	
	public ConnectionNode getInputConnection()
	{
		if (startConnection != null && startConnection.isInputConnection())
		{
			return startConnection;
		}
		if (endConnection != null && endConnection.isInputConnection())
		{
			return endConnection;
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
		return startPoint + " -> " + endPoint + "(" + id + ")";
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
		updateWires(circuit);
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