package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Graphics;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.HashMap;

import wit.edu.yeatesg.simulator.objects.abstractt.SignalEntity;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalReceiver;
import wit.edu.yeatesg.simulator.objects.abstractt.SignalSender;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
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
	
	int numTimesWireHasBeenCreated = 0;
	
	public Wire(BigPoint startPoint, BigPoint endPoint, SignalEntity startConnection, SignalEntity endConnection, Circuit circuit)
	{
		System.out.println(++numTimesWireHasBeenCreated + " many wires have been created");
		assert startPoint.x == endPoint.x || startPoint.y == endPoint.y;
		horizontal = startPoint.x == endPoint.x ? false : true;
		
		this.circuit = circuit;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startConnection = startConnection;
		this.endConnection = endConnection;
		
		connectedJunctions = new ArrayList<>();
		
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
		
		Wire.updateWires(circuit);
	}
	
	public static void updateWires(Circuit circuit)
	{	
		WireJunction.removeAllWireJunctions(circuit);
		System.out.println("removeWiresJunctions()");
		
		for (Wire w : circuit.getAllWires())
		{
			w.connectedJunctions.clear();
		}
		
		for (Wire w : circuit.getAllWires())
		{
			ArrayList<Wire> startPointInterceptions = Wire.wiresThatHaveAnEdgePointAt(w.startPoint, w.circuit);
			ArrayList<Wire> endPointInterceptions = Wire.wiresThatHaveAnEdgePointAt(w.endPoint, w.circuit);
			BigPoint interceptPoint = null;
			if (startPointInterceptions.size() >= 3)
			{
				System.out.println(startPointInterceptions.size());
				System.out.println(circuit.getAllWires().size());
				interceptPoint = w.startPoint.clone();
				for (Wire wireToConnect : startPointInterceptions)
				{
					if (!wireToConnect.hasJunctionAt(interceptPoint))
					{
						wireToConnect.connectJunction(interceptPoint);
					}
				}
			}
			if (endPointInterceptions.size() >= 3)
			{
				interceptPoint = w.endPoint.clone();
			}
			
		}
		circuit.refreshTransmissions();
	}
	
	public void connectJunction(BigPoint p)
	{
		if (WireJunction.hasJunctionAt(p, circuit))
		{
			System.out.print("has");
			WireJunction junc = WireJunction.getJunctionAt(p, circuit);
			junc.connectToWire(this);
			connectedJunctions.add(junc);
		}
		else
		{
			System.out.println("IMA BUST " + p);
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

	
	@Override
	public void draw(Graphics g)
	{
		EditorPanel panel = circuit.getEditorPanel();
		GraphicsTools graphics = new GraphicsTools(panel, g);
		System.out.println(Color.GREEN.getRed() + " " + Color.GREEN.getGreen() + " " + Color.GREEN.getBlue());
		int extraThicc = circuit.getGapBetweenPoints() / 8;
		if (!horizontal) graphics.drawVerticalLine(startPoint, endPoint, g, extraThicc + circuit.getGridPointDrawOffset().x);
		else graphics.drawHorizontalLine(startPoint, endPoint, g, extraThicc + circuit.getGridPointDrawOffset().x);
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
	public void onDelete() {
		// TODO Auto-generated method stub
		
	}
}