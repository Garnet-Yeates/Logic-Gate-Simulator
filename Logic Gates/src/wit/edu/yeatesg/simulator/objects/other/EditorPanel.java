package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import wit.edu.yeatesg.simulator.objects.abstractt.Entity;
import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Vector;

public class EditorPanel extends JPanel implements MouseListener, KeyListener, MouseMotionListener, ComponentListener
{
	private static final long serialVersionUID = 5754945305788423567L;

	public Circuit circuit;
	public Frame frame;

	private int width = 800;
	private int height = 800;

	private boolean init = true;
	
	public EditorPanel(Frame container)
	{
		this.circuit = new Circuit(this);
		frame = container;
		setSize(800, 800);
		setVisible(true);
		addComponentListener(this);

		circuit.modifyOffset(new Vector(width / 2, height / 2));

		Wire w1 = new Wire(new BigPoint(5, 5), new BigPoint(5, 10), circuit);
		Wire w2 = new Wire(new BigPoint(5, 8), new BigPoint(10, 8), circuit);
		Wire w3 = new Wire(new BigPoint(5, 5), new BigPoint(20, 5), circuit);
		Wire w4 = new Wire(new BigPoint(9, 5), new BigPoint(9, 40), circuit);
		Wire w5 = new Wire(new BigPoint(7, 0), new BigPoint(7, 20), circuit);

		w1.transmit();
		init = false;
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(248, 248, 248));
		g.fillRect(0, 0, width, height);

		drawBigPoints(g);
		drawEntities(g);
		g.setColor(Color.BLACK);
		drawConnectionSelectionPreview(g);
		drawSelectedEntityPoints(g);
	}

	private void drawBigPoints(Graphics g)
	{
		for (int xPos = 0; xPos <= width; xPos += circuit.getGapBetweenPoints())
		{
			for (int yPos = 0; yPos <= height; yPos += circuit.getGapBetweenPoints())
			{
				BigPoint p = BigPoint.fromLittlePoint(LittlePoint.getEditorCoords(new LittlePoint(xPos, yPos), circuit), circuit);
				g.setColor(p.x == 0 && p.y == 0 ? Color.RED : Color.GRAY);
				p.draw(g, circuit);
			}
		}
	}

	private void drawEntities(Graphics g)
	{
		if (!init)
		{
			for (Entity e : circuit.getAllEntities())
			{
				if (e.withinDrawingBounds())
				{
					e.draw(g);
				}
			}
		}
	}
	
	private void drawConnectionSelectionPreview(Graphics g)
	{
		if (mousingOverSelectionNode)
		{
			LittlePoint p = LittlePoint.getPanelCoords((LittlePoint.fromBigPoint(closestBigPoint, circuit)), circuit);
			int drawSize = circuit.getGridPointDrawSize()*4;
			int offset = drawSize / -2;
			for (int thickness = circuit.getGapBetweenPoints() / 5; thickness > 0; thickness--, offset -= 1, drawSize += 2)
			{
				g.drawOval(p.x + offset, p.y + offset, drawSize, drawSize);
			}
		}
	}
	
	private Entity selectedEntity = null;
	
	private void drawSelectedEntityPoints(Graphics g)
	{
		if (selectedEntity != null)
		{
			selectedEntity.drawSelectionIndicator(g);
		}
	}

	public void zoom(boolean in)
	{
		LittlePoint panelCenter = LittlePoint.getEditorCoords(new LittlePoint(width / 2, height / 2), circuit);
		BigPoint bigPanelCenter = BigPoint.fromLittlePoint(panelCenter, circuit);
		if (circuit.zoom(in))
		{
			panelCenter = LittlePoint.fromBigPoint(bigPanelCenter, circuit);
			circuit.resetOffset();
			circuit.modifyOffset(new Vector(width / 2, height / 2));
			circuit.modifyOffset(new Vector(panelCenter.x / 2, panelCenter.y / 2).multiply(-1));
		}
		repaint();
	}

	public Circuit getCircuit()
	{
		return circuit;
	}

	private LittlePoint lastMouseLocation_pc;

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (middleClickHeld || holdingSpce) // The user is dragging the screen to move it
		{
			Vector dragVector = new Vector(lastMouseLocation_pc, LittlePoint.getPanelCoordinates(e)).multiply(1);
			lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
			circuit.modifyOffset(dragVector);
		}


		repaint();
	}

	private BigPoint closestBigPoint;
	private boolean mousingOverSelectionNode;

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (holdingSpce) // The user is dragging the screen to move it
		{
			Vector dragVector = new Vector(lastMouseLocation_pc, LittlePoint.getPanelCoordinates(e));
			lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
			circuit.modifyOffset(dragVector);
		}	

		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		closestBigPoint = BigPoint.closestTo(LittlePoint.getEditorCoords(lastMouseLocation_pc, circuit), circuit);

		mousingOverSelectionNode = closestBigPoint.hasInterceptingConnectable(circuit);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		System.out.println(LittlePoint.getEditorCoords(LittlePoint.getPanelCoordinates(e), circuit));
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean middleClickHeld = false;
	private Entity connectionDraggingFrom;
	private BigPoint lastMousePressLoc = null;

	@Override
	public void mousePressed(MouseEvent e)
	{	
		lastMousePressLoc = BigPoint.fromMouseEvent(e, circuit).round();
		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		middleClickHeld = e.getButton() == 2 ? true : false;

		if (mousingOverSelectionNode)
		{
			connectionDraggingFrom = closestBigPoint.getInterceptingEntity(circuit);
		}
		else // if mousing over hitbox...
		{
			System.out.println("");
			// TODO check and see if they clicked in a hitbox and shit 
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		selectedEntity = null;
		middleClickHeld = e.getButton() == 2 ? false : true;

		BigPoint releasePoint = BigPoint.fromMouseEvent(e, circuit).round();
		BigPoint pressPoint = lastMousePressLoc;

		if (connectionDraggingFrom != null && !pressPoint.equals(releasePoint))
		{
			// The user dragged some connection (WireJunction > Wire > ConnectionNode) (showing priority of what is selected)
			// from pressPoint to releasePoint
			System.out.println("DRAGGED SOME CONNECTION FROM HERE TO THERE " + pressPoint + " TO " + releasePoint);

			if (connectionDraggingFrom instanceof WireJunction)
			{
				WireJunction junc = (WireJunction) connectionDraggingFrom;

				Wire deletingOrTrimming = null;
				for (Wire w : junc.getConnectedWires())
					if (w.intercepts(releasePoint))
						deletingOrTrimming = w;
				// If pressPoint and releasePoint both intercept the same wire on the junction, then that
				// wire is being deleted or trimmed down

				if (deletingOrTrimming != null)
				{
					// In this case they are trimming a wire or deleting a wire from a junction
					negativeWireModification(pressPoint, releasePoint, deletingOrTrimming);
				}
				else
				{
					// In this case they are creating a new wire at a junction
					positiveWireModification(pressPoint, releasePoint, null);
				}	
			}
			else if (connectionDraggingFrom instanceof Wire) // If they are dragging on ONE existing wire (not a junction)
			{
				Wire wireBeingDragged = (Wire) connectionDraggingFrom;
				BigPoint startPoint = wireBeingDragged.getStartPoint();
				BigPoint endPoint = wireBeingDragged.getEndPoint();

				// If it intercepts itself at any point besides the start point and it isnt being deleted then
				// a wire should NOT be created

				boolean grabbedFromAnyEndPoint = pressPoint.equals(startPoint) || pressPoint.equals(endPoint);
				if (grabbedFromAnyEndPoint && releasePoint.intercepts(wireBeingDragged))
				{
					// In this case they are deleting/shortening a wire (a negative wire modification)
					negativeWireModification(pressPoint, releasePoint, wireBeingDragged);
				}
				else if (!releasePoint.intercepts(wireBeingDragged)) // They r extending wire
				{
					// In this case they are extending/creating a wire (a positive wire modification)
					positiveWireModification(pressPoint, releasePoint, wireBeingDragged);

				}
				int extraThicc = circuit.getGapBetweenPoints() / 9;
			}
			else if (connectionDraggingFrom instanceof ConnectionNode)
			{

			}
		}
		else if (pressPoint.equals(releasePoint) && connectionDraggingFrom != null)
		{
			// Regular click on a connectable (WireJunction > Wire > ConnectionNode)
			selectedEntity = connectionDraggingFrom;
			System.out.println("REGG CLICK ON A CONNECTABLE NODE");
		}
		else if (pressPoint.equals(releasePoint))
		{
			// Regular click on nothing in particular
			selectedEntity = null;
			// TODO later on this needs to look at all this hitboxes of non WireJunction, Wire, and ConnectionNode
			// Entities and see if the click was within its hitbox, and if so it needs to be selected
			System.out.println("REGG U LARR CLICK AT " + pressPoint);
		}
		else
		{
			// Regular drag from pressPoint to releasePoint (such as a selection box)
			System.out.println("REGULAR DRAG FROM HERETO HERE " + pressPoint + " TO " + releasePoint);
		}
		connectionDraggingFrom = null;
		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		lastMousePressLoc = BigPoint.fromMouseEvent(e, circuit).round();
		closestBigPoint = BigPoint.closestTo(LittlePoint.getEditorCoords(lastMouseLocation_pc, circuit), circuit);
		mousingOverSelectionNode = closestBigPoint.hasInterceptingConnectable(circuit);
		repaint();
	}

	public void negativeWireModification(BigPoint pressPoint, BigPoint releasePoint, Wire w)
	{
		BigPoint startPoint = w.getStartPoint();
		BigPoint endPoint = w.getEndPoint();
		boolean grabbedAtStartPoint = pressPoint.equals(startPoint);
		boolean grabbedAtEndPoint = !grabbedAtStartPoint;		

		if ((grabbedAtStartPoint && releasePoint.equals(endPoint)) || grabbedAtEndPoint && releasePoint.equals(startPoint))
		{
			// DELETE THE WIRE
			System.out.println("DELETEE THE FUCKING WIRE");
		}
		else
		{
			if (grabbedAtStartPoint)
			{
				// SET THE START POINT OF THE WIRE TO releasePoint
				// DISCONNECT startPoint connection from wire
				System.out.println("MOVE START POINT AND SHIT");

			}
			else
			{
				// SET THE END POINT OF THE WIRE TO releasePoint
				// DISCONNECT endPoint connection from wire
				System.out.println("MOVE END POINT AND SHIT");
			}
		}
	}

	public void positiveWireModification(BigPoint pressPoint, BigPoint releasePoint, Wire w)
	{
		if (canPositiveWireModification(pressPoint, releasePoint, w))
		{
			Wire edited = null;
			if (w == null) // This means pressPoint was a junction so no wires are being extended, only created
			{
				edited = new Wire(pressPoint, releasePoint, circuit);
				System.out.println("NEW WIRE FROM JUNCTION");
			}
			else
			{
				edited = w;

				BigPoint startPoint = w.getStartPoint();
				BigPoint endPoint = w.getEndPoint();
				boolean grabbedAtEndPoint = pressPoint.equals(endPoint);
				boolean grabbedAtStartPoint = pressPoint.equals(startPoint);

				if (grabbedAtStartPoint && ((endPoint.x == startPoint.x && endPoint.x == releasePoint.x)
				|| (endPoint.y == startPoint.y && endPoint.y == releasePoint.y)))
				{
					w.setStartPoint(releasePoint);
					Wire.wireBisectCheck(circuit);
					System.out.println("NEW WIRE FROM EXTENDING STARTPOINT");
					
				}
				else if (grabbedAtEndPoint && ((endPoint.x == startPoint.x && endPoint.x == releasePoint.x)
				|| (endPoint.y == startPoint.y && endPoint.y == releasePoint.y)))
				{
					w.setEndPoint(releasePoint);
					Wire.wireBisectCheck(circuit);
					System.out.println("NEW WIRE FROM EXTENDING ENDPOINT");
				}
				else
				{
					edited = new Wire(pressPoint, releasePoint, circuit);
					System.out.println("NEW WIRE FROM WIRE OR CONNECTION NODE");
				}

				if (releasePoint.hasInterceptingConnectionNode(circuit))
				{
					ConnectionNode connection = (ConnectionNode) releasePoint.getInterceptingEntity(circuit);
					connection.connect(edited);
				}
			}			
		}
		Wire.updateWires(circuit);
	}

	public boolean canPositiveWireModification(BigPoint pressPoint, BigPoint releasePoint, Wire w)
	{
		// CANT INTERCEPT A WIRE THAT IS GOING IN THE SAME DIRECTION AS IT (UNLESS INTERCEPTING ENDPOINTS)
		ArrayList<BigPoint> betweenPoints = BigPoint.getPointsBetween(pressPoint, releasePoint);
		boolean goingHorizontal = pressPoint.y == releasePoint.y;
		if (betweenPoints == null) return false;
		for (BigPoint p : betweenPoints)
		{
			if (p.hasInterceptingWireJunction(circuit))
			{
				return false;
			}
			if (p.hasInterceptingWire(circuit))
			{
				Wire intercepting = (Wire) p.getInterceptingEntity(circuit);
				if ( (intercepting.isHorizontal() && goingHorizontal) || (!intercepting.isHorizontal()) && !goingHorizontal)
				{
					return false;
				}
			}
		}
		
		// The only thing that the end point of this new wire can be on is an empty space, or a connectable space
		boolean endPointValid = !releasePoint.hasInterceptingEntity(circuit) || releasePoint.hasInterceptingConnectable(circuit);
		
		// The start point is already checked in earlier methods, but if w is null (meaning the startpoint was a junction), then we have to make sure the junction isnt full
		boolean startPointValid = w != null || ((WireJunction) pressPoint.getInterceptingEntity(circuit)).getConnectedWires().size() < 4;
		
		// Make sure they are drawing a straight line for the Wire
		boolean validWire = pressPoint.x == releasePoint.x || pressPoint.y == releasePoint.y;
		
		return startPointValid && endPointValid && validWire;		
	}

	boolean holdingCtrl = false;
	boolean holdingSpce = false;

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_SPACE:
			holdingSpce = true;
			break;
		case KeyEvent.VK_MINUS:
			zoom(false);
			break;
		case KeyEvent.VK_EQUALS:
			if (holdingCtrl) zoom(true);
			break;
		case KeyEvent.VK_CONTROL:
			holdingCtrl = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_CONTROL:
			holdingCtrl = false;
			break;
		case KeyEvent.VK_SPACE:
			holdingSpce = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		System.out.println(width + " " + height + "  :  " + getWidth() + " " + getHeight());
		int widthDiff = getWidth() - width;
		int heightDiff = getHeight() - height;
		circuit.modifyOffset(new Vector(widthDiff, heightDiff));
		width = getWidth();
		height = getHeight();
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub	
	}
}
