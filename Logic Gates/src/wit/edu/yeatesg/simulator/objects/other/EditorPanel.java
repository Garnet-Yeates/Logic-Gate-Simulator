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
	
	public EditorPanel(Frame container)
	{
		this.circuit = new Circuit(this);
		frame = container;
		setSize(800, 800);
		setVisible(true);
		addComponentListener(this);

		circuit.modifyOffset(new Vector(width / 2, height / 2));

		Wire w1 = new Wire(new BigPoint(5, 5), new BigPoint(5, 10), circuit);
		w1.getBottomPoint();
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		drawBackground(g);
		drawBigPoints(g);
		drawEntities(g);
		drawSelectedEntityPoints(g);
		if (!drawTheoreticalWireChanges(g)) drawConnectionSelectionPreview(g);
	}
	
	private Color backgroundColor = new Color(248, 248, 248);
	
	private void drawBackground(Graphics g)
	{
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
	}
	
	private Color bigPointColor = Color.GRAY;

	/**
	 * Draws all of the grid dots on the display panel. 
	 * @param g
	 */
	private void drawBigPoints(Graphics g)
	{
		for (int xPos = 0; xPos <= width + circuit.getGapBetweenPoints(); xPos += circuit.getGapBetweenPoints())
		{
			for (int yPos = 0; yPos <= height + circuit.getGapBetweenPoints(); yPos += circuit.getGapBetweenPoints())
			{
				LittlePoint editorCoords = LittlePoint.getEditorCoords(new LittlePoint(xPos, yPos), circuit);
				BigPoint p = BigPoint.fromLittlePoint(editorCoords, circuit);
				g.setColor((int) p.x == 0 && (int) p.y == 0 ? Color.RED : bigPointColor);
				p.draw(g, circuit);
			}
		}
	}

	private void drawEntities(Graphics g)
	{
		for (Entity e : circuit.getAllEntities())
		{
			if (e.withinDrawingBounds())
			{
				e.draw(g);
			}
		}
	}
	
	private void drawConnectionSelectionPreview(Graphics g)
	{
		g.setColor(Color.BLACK);
		if (mousingOverConnectable)
		{
			LittlePoint p = LittlePoint.getPanelCoords((LittlePoint.fromBigPoint(gridSnapPoint, circuit)), circuit);
			int drawSize = circuit.getGridPointDrawSize()*4;
			int offset = drawSize / -2;
			for (int thickness = circuit.getGapBetweenPoints() / 5; thickness > 0; thickness--, offset -= 1, drawSize += 2)
			{
				g.drawOval(p.x + offset, p.y + offset, drawSize, drawSize);
			}
		}
	}
	
	private Entity selectedEntity = null;
	
	/**
	 * Draws the corners of the selection box around the currently selected Entity. For example, if a LogicGate is
	 * currently selected, then the four corners of its selection box will be drawn around it. If a wire is selected,
	 * then it will draw the two end points so the user can see them.
	 * @param g the Graphics instance being used to draw this
	 */
	private void drawSelectedEntityPoints(Graphics g)
	{
		if (selectedEntity != null)
		{
			selectedEntity.drawSelectionIndicator(g);
		}
	}
	
	 /** The last updated panel coordinate location of the mouse in the program. Updated in the mouse moved, dragged, pressed, and release methods*/
	private LittlePoint lastMouseLocation_pc;

	 /** The closest BigPoint to the cursor, updated whenever {@link #mouseMoved(MouseEvent)} or {@link #mouseReleased(MouseEvent)} are called*/
	private BigPoint gridSnapPoint;
	
	 /**  Whether or not the player is mousing over something that they can drag a wire from. Updated whenever {@link #mouseMoved(MouseEvent)} and {@link #mouseReleased(MouseEvent)} are called*/
	private boolean mousingOverConnectable;
	
	/**
	 * Called whenever the mouse is moved. If the user is holding space then offsets will be adjusted to move the
	 * domain of the coordinates around. This method also updates the {@link #lastMouseLocation_pc}, {@link #gridSnapPoint},
	 * and {@link #mousingOverConnectable} methods, and repaints the editor panel.
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (holdingSpce) // The user is dragging the screen to move it
		{
			Vector dragVector = new Vector(lastMouseLocation_pc, LittlePoint.getPanelCoordinates(e));
	//		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
			circuit.modifyOffset(dragVector);
		}	

		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		gridSnapPoint = BigPoint.closestTo(LittlePoint.getEditorCoords(lastMouseLocation_pc, circuit), circuit);

		mousingOverConnectable = gridSnapPoint.hasInterceptingConnectable(circuit);
		repaint();
	}

	/**
	 * Called whenever the mouse is dragged. If the user is middle clicking then this will drag the screen (much like holding
	 * space does in the {@link #mouseMoved(MouseEvent)} method). This method also detects if the user is dragging a wire from
	 * a connection point, which means that they are either creating/deleting/extending/shortening a wire. If this is happening,
	 * then this will call {@link #theoreticalPositiveWireModification(BigPoint, BigPoint, Wire)} if a wire is being created or
	 * extended, or {@link #theoreticalNegativeWireModification(BigPoint, BigPoint)} otherwise.
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (middleClickHeld) // The user is dragging the screen to move it
		{
			Vector dragVector = new Vector(lastMouseLocation_pc, LittlePoint.getPanelCoordinates(e)).multiply(1);
			lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
			circuit.modifyOffset(dragVector);
		}
		
		BigPoint pressPoint = lastMousePressLoc;
		BigPoint theoreticalReleasePoint = BigPoint.fromMouseEvent(e, circuit).round();
		
		if (theoreticalReleasePoint.x == pressPoint.x || theoreticalReleasePoint.y == pressPoint.y)
		{
			if (connectionDraggingFrom != null && !pressPoint.equals(theoreticalReleasePoint))
			{
				if (connectionDraggingFrom instanceof WireJunction)
				{
					WireJunction junc = (WireJunction) connectionDraggingFrom;

					Wire deletingOrTrimming = null;
					for (Wire w : junc.getConnectedWires())
						if (w.intercepts(theoreticalReleasePoint))
							deletingOrTrimming = w;

					if (deletingOrTrimming != null)
					{
						theoreticalNegativeWireModification(pressPoint, theoreticalReleasePoint);
					}
					else
					{
						if (betweenPointsValid(pressPoint, theoreticalReleasePoint))
						{
							theoreticalPositiveWireModification(pressPoint, theoreticalReleasePoint, null);
						}
					}	
				}
				else if (connectionDraggingFrom instanceof Wire) // If they are dragging on ONE existing wire (not a junction)
				{
					Wire wireBeingDragged = (Wire) connectionDraggingFrom;
					BigPoint startPoint = wireBeingDragged.getStartPoint();
					BigPoint endPoint = wireBeingDragged.getEndPoint();

					boolean grabbedFromAnyEndPoint = pressPoint.equals(startPoint) || pressPoint.equals(endPoint);
					if (grabbedFromAnyEndPoint && theoreticalReleasePoint.intercepts(wireBeingDragged))
					{
						theoreticalNegativeWireModification(pressPoint, theoreticalReleasePoint);
					}
					else if (!theoreticalReleasePoint.intercepts(wireBeingDragged)) // They r extending wire
					{
						theoreticalPositiveWireModification(pressPoint, theoreticalReleasePoint, wireBeingDragged);
					}
				}
				else if (connectionDraggingFrom instanceof ConnectionNode)
				{
					// TODO We still need to handle this
				}
			}
		}
		repaint();
	}
	
	/** Whenever the user presses down on the mouse on a connection, this will be set to a non null entity. Upon release, it is set to null again*/
	private Entity connectionDraggingFrom;
	
	/** The last updated location where the player clicked or un-clicked the mouse in mousePressed and mouseReleased methods*/
	private BigPoint lastMousePressLoc = null;
	
	/**
	 * Called whenever the mouse is pressed in this EditorPanel. This method updates the 
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{	
		lastMousePressLoc = BigPoint.fromMouseEvent(e, circuit).round();
		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		middleClickHeld = e.getButton() == 2 ? true : false;

		if (mousingOverConnectable)
		{
			connectionDraggingFrom = gridSnapPoint.getInterceptingEntity(circuit);
		}
		else // if mousing over hitbox...
		{
			// TODO check and see if they clicked in a hitbox and shit 
		}
	}

	/**
	 * Called whenever the mouse is released. If the user was dragging from a connection (aka creating/destroying/extending/shortening a wire)
	 * before the mouse is release, then this method will figure out what type of wire modification they are doing, and attempt that
	 * modification. This method also updates the {@link #lastMouseLocation_pc}, {@link #lastMousePressLoc}, {@link #gridSnapPoint}, and
	 * {@link #mousingOverConnectable} methods, and sets {@link #connectionDraggingFrom} to null. Read comments in method for better understanding.
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		selectedEntity = null;
		middleClickHeld = e.getButton() == 2 ? false : true;

		BigPoint releasePoint = BigPoint.fromMouseEvent(e, circuit).round();
		BigPoint pressPoint = lastMousePressLoc;

		if (pressPoint.x == releasePoint.x || pressPoint.y == releasePoint.y)
		{
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
				}
				else if (connectionDraggingFrom instanceof ConnectionNode)
				{

				}
			}
			else if (pressPoint.equals(releasePoint) && connectionDraggingFrom != null)
			{
				// Regular click on a connectable (WireJunction > Wire > ConnectionNode)
				selectedEntity = connectionDraggingFrom;
				System.out.println("SELECTED " + selectedEntity);
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
		}
		
		connectionDraggingFrom = null;
		lastMouseLocation_pc = LittlePoint.getPanelCoordinates(e);
		lastMousePressLoc = BigPoint.fromMouseEvent(e, circuit).round();
		gridSnapPoint = BigPoint.closestTo(LittlePoint.getEditorCoords(lastMouseLocation_pc, circuit), circuit);
		mousingOverConnectable = gridSnapPoint.hasInterceptingConnectable(circuit);
		repaint();
	}

	/** 
	 * If the user is in the process of deleting/adding/moving a wire (before they release the mouse), this field will let the program
	 * know what type of modification they are doing to the wire (so that the operation preview can be displayed to the user) true means
	 * that this is a positive wire modification*/
	private boolean wireModType;
	
	/** If the user is in the process of deleting/adding/moving a wire this is the press point of their operation*/
	private BigPoint wireModStart;
	
	/** If the user is in the process of deleting/adding/moving a wire this is the release point of their operation*/
	private BigPoint wireModEnd;
	
	public void theoreticalNegativeWireModification(BigPoint pressPoint, BigPoint releasePoint)
	{
		System.out.println("DISPLAY NEG MOD");
		wireModType = false;
		wireModStart = pressPoint;
		wireModEnd = releasePoint;
	}
	
	public void theoreticalPositiveWireModification(BigPoint pressPoint, BigPoint releasePoint, Wire w)
	{
		if (w == null || canPositiveWireModification(pressPoint, releasePoint, w))
		{
			System.out.println("DISPLAY POS MOD");
			wireModType = true;
			wireModStart = pressPoint;
			wireModEnd = releasePoint;
		}
	}
	
	/**
	 * While the user is dragging the mouse towards/away from a wire to modify it (during the {@link #mouseDragged(MouseEvent)}
	 * event), the method checks to see if the user is currently extending, shortening, or deleting a wire. If the
	 * method determines that this is happening, then the fields {@link #wireModType}, {@link #wireModStart}, and {@link #wireModEnd}
	 * are updated to let the program know what type of modification is about to be made, and where it begins and ends. After these
	 * fields are updated, the mouseDragged method will also call {@link #repaint()}, which will call this method. What this method does
	 * is draws a "preview" of the modification that the user is currently doing to a wire. If they are extending the wire, it will
	 * preview a new wire being made, and if they are deleting or shortening a wire it will preview that wire being deleted. If
	 * {@link #wireModEnd} and {@link #wireModStart} are null then the user isn't currently modifying a wire, or they are modifying
	 * a wire in an invalid way
	 * @param g the Graphics instance used to draw this wire
	 * @return false if no theoretical wire changes are being drawn
	 */
	private boolean drawTheoreticalWireChanges(Graphics g)
	{
		if (wireModStart != null && wireModEnd != null && (wireModStart.x == wireModEnd.x || wireModStart.y == wireModEnd.y))
		{
			boolean horizontal = wireModStart.y == wireModEnd.y;
			int thickness = circuit.getGapBetweenPoints() / 9;
			g.setColor(wireModType ? Color.BLACK : backgroundColor);

			if (horizontal)
			{
				GraphicsTools.drawHorizontalLine(wireModStart, wireModEnd, thickness + circuit.getGridPointDrawOffset(), g, circuit);
			}
			else
			{
				GraphicsTools.drawVerticalLine(wireModStart, wireModEnd, thickness + circuit.getGridPointDrawOffset(), g, circuit);
			}
			
			if (!wireModType)
			{
				g.setColor(Color.GRAY);
				wireModStart.draw(g, circuit);
				if (BigPoint.getPointsBetween(wireModStart, wireModEnd).size() > 0)
				{
					for (BigPoint p : BigPoint.getPointsBetween(wireModStart, wireModEnd))
					{
						p.draw(g, circuit);
					}
				}
				
				if (wireModEnd.hasInterceptingWireJunction(circuit))
				{
					WireJunction j = (WireJunction) wireModEnd.getInterceptingEntity(circuit);
					j.draw(g);
				}
			}
		}
		else
		{
			return false;
		}
		
		wireModStart = null;
		wireModEnd = null;
		return true;
	}

	public void negativeWireModification(BigPoint pressPoint, BigPoint releasePoint, Wire w)
	{
		BigPoint startPoint = w.getStartPoint();
		BigPoint endPoint = w.getEndPoint();
		boolean grabbedAtStartPoint = pressPoint.equals(startPoint);
		boolean grabbedAtEndPoint = !grabbedAtStartPoint;		

		if ((grabbedAtStartPoint && releasePoint.equals(endPoint)) || grabbedAtEndPoint && releasePoint.equals(startPoint))
		{
			w.delete();
		}
		else
		{
			if (grabbedAtStartPoint)
			{
				w.setStartPoint(releasePoint);
			}
			else
			{
				w.setEndPoint(releasePoint);
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
//				System.out.println("NEW WIRE FROM JUNCTION");
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
			//		Wire.wireBisectAndConnectionCheck(circuit);
//					System.out.println("NEW WIRE FROM EXTENDING STARTPOINT");
					
				}
				else if (grabbedAtEndPoint && ((endPoint.x == startPoint.x && endPoint.x == releasePoint.x)
				|| (endPoint.y == startPoint.y && endPoint.y == releasePoint.y)))
				{
					w.setEndPoint(releasePoint);
				//	Wire.wireBisectAndConnectionCheck(circuit);
//					System.out.println("NEW WIRE FROM EXTENDING ENDPOINT");
				}
				else
				{
					edited = new Wire(pressPoint, releasePoint, circuit);
//					System.out.println("NEW WIRE FROM WIRE OR CONNECTION NODE");
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
		// CANT INTERCEPT A JUNCTION OR A WIRE THAT IS GOING IN THE SAME DIRECTION AS IT (UNLESS INTERCEPTING ENDPOINTS)
		boolean betweenPointsValid = betweenPointsValid(pressPoint, releasePoint) || pressPoint.isAdjacentTo(releasePoint);
		
		// The only thing that the end point of this new wire can be on is an empty space, or a connectable space
		boolean endPointValid = !releasePoint.hasInterceptingEntity(circuit) || releasePoint.hasInterceptingConnectable(circuit);
		
		// The start point is already checked in earlier methods, but if w is null (meaning the startpoint was a junction), then we have to make sure the junction isnt full
		boolean startPointValid = w != null || ((WireJunction) pressPoint.getInterceptingEntity(circuit)).getConnectedWires().size() < 4;
		
		// Make sure they are drawing a straight line for the Wire
		boolean validWire = pressPoint.x == releasePoint.x || pressPoint.y == releasePoint.y;
		
		return startPointValid && betweenPointsValid && endPointValid && validWire;		
	}

	public boolean betweenPointsValid(BigPoint pressPoint, BigPoint releasePoint)
	{
		ArrayList<BigPoint> betweenPoints = BigPoint.getPointsBetween(pressPoint, releasePoint);
		boolean goingHorizontal = pressPoint.y == releasePoint.y;
		
		if (betweenPoints.size() > 0)
		{
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
		}
		else return false;
		return true;
	}

	/**
	 * Zooms the entire circuit in. This makes it so less grid points (BigPoints) are on the screen at once and the distance
	 * between them is greater, so you can see closer up/more detail in the circuit. This method also keeps the screen centered
	 * around whatever it was centered around before it was zoomed in
	 * @param in if this is false, then the circuit is zooming out
	 */
	public void zoom(boolean in)
	{
		LittlePoint panelCenter = LittlePoint.getEditorCoords(new LittlePoint(width / 2, height / 2), circuit);
		BigPoint bigPanelCenter = BigPoint.fromLittlePoint(panelCenter, circuit);
		if (circuit.zoom(in))
		{
			panelCenter = LittlePoint.fromBigPoint(bigPanelCenter, circuit);
			circuit.resetOffset();
			circuit.modifyOffset(new Vector(width / 2, height / 2));
			circuit.modifyOffset(new Vector(panelCenter.x, panelCenter.y).multiply(-1));
		}
		repaint();
	}

	/**
	 * Obtains the circuit that is being drawn onto this EditorPanel for display
	 * @return the circuit, which contains all of the entities, offsets, and scales that are displayed on this EditorPanel
	 */
	public Circuit getCircuit()
	{
		return circuit;
	}

	/**  */
	private boolean middleClickHeld = false;


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
}
