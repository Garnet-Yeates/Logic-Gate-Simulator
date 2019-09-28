package wit.edu.yeatesg.simulator.objects.other;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
	
	private int prefWidth = 800;
	private int prefHeight = 800;
	
	public EditorPanel(Frame container)
	{
		this.circuit = new Circuit(this);
		frame = container;
		setSize(800, 800);
		setVisible(true);
		addComponentListener(this);
		
	//	adjustDimensions();
		
		circuit.modifyOffset(new Vector(width / 2, height / 2));
		
		Wire w1 = new Wire(new BigPoint(5, 5), new BigPoint(5, 10), circuit);
		Wire w2 = new Wire(new BigPoint(5, 8), new BigPoint(10, 8), circuit);
		Wire w3 = new Wire(new BigPoint(5, 5), new BigPoint(20, 5), circuit);
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, 0, width, height);
		for (int xPos = 0; xPos <= width; xPos += circuit.getGapBetweenPoints())
		{
			// If u shift left by 1, the x offset shud now be 1
			for (int yPos = 0; yPos <= height; yPos += circuit.getGapBetweenPoints())
			{
				int xOff = circuit.getOffset().x;
				int yOff = circuit.getOffset().y;
				
				xOff = xOff % width;
				xOff = xOff % (circuit.getGapBetweenPoints());
				
				yOff = yOff % height;
				yOff = yOff % (circuit.getGapBetweenPoints());

				g.setColor(Color.GRAY);
				LittlePoint drawLocation = new LittlePoint(xPos + xOff, yPos + yOff);
				drawLocation.addOffset(circuit.getGridPointDrawOffset());
				g.fillRect((int)drawLocation.x, (int)drawLocation.y, circuit.getGridPointDrawSize(), circuit.getGridPointDrawSize());
			}

			g.setColor(Color.RED);
			LittlePoint origin = editorCoordsToPanelCoords(new LittlePoint(0, 0));
			origin.addOffset(circuit.getGridPointDrawOffset());
			g.fillRect((int)origin.x, (int)origin.y, circuit.getGridPointDrawSize(), circuit.getGridPointDrawSize());

			for (Entity e : circuit.getEntities())
			{
				e.draw(g);
			}
		}
	}
	
	public void zoom(boolean in)
	{
		LittlePoint panelCenter = panelCoordsToEditorCoords(new LittlePoint(width / 2, height / 2));
		BigPoint bigPanelCenter = fromLittlePoint(panelCenter);
		if (circuit.zoom(in))
		{
			panelCenter = fromBigPoint(bigPanelCenter);
			circuit.resetOffset();
			circuit.modifyOffset(new Vector(width / 2, height / 2));
			circuit.modifyOffset(new Vector(panelCenter.x / 2, panelCenter.y / 2).multiply(-1));
		}
		repaint();
	//	adjustDimensions();
	}
	
	public Circuit getCircuit()
	{
		return circuit;
	}
	
	public LittlePoint fromBigPoint(BigPoint p)
	{
		return new LittlePoint((int) p.x * circuit.getGapBetweenPoints(),(int) p.y * circuit.getGapBetweenPoints());
	}
	
	public BigPoint fromLittlePoint(LittlePoint p)
	{
		return new BigPoint(p.x / circuit.getGapBetweenPoints(), p.y / circuit.getGapBetweenPoints());
	}
	
	private LittlePoint clickOffset = new LittlePoint(-8, -31);
	
	public LittlePoint getPanelCoordinates(MouseEvent e)
	{
		int x = (int) (e.getX() + clickOffset.x);
		int y = (int) (e.getY() + clickOffset.y);
		
		return new LittlePoint(x, y);
	}
	
	public LittlePoint panelCoordsToEditorCoords(LittlePoint p)
	{
		int x = (int) (p.x - circuit.getOffset().x);
		int y = (int) (p.y - circuit.getOffset().y);
		
		return new LittlePoint(x, y);
	}
	
	public LittlePoint editorCoordsToPanelCoords(LittlePoint p)
	{
		int x = (int) (p.x + circuit.getOffset().x);
		int y = (int) (p.y + circuit.getOffset().y);
		
		return new LittlePoint(x, y);
	}

	private LittlePoint lastDraggedLocation;
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (middleClickHeld || holdingSpce)
		{
			Vector dragVector = new Vector(lastDraggedLocation, getPanelCoordinates(e)).multiply(1);
			lastDraggedLocation = getPanelCoordinates(e);
			circuit.modifyOffset(dragVector);
			repaint();
		}		
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		
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
	public void mouseClicked(MouseEvent e)
	{
		System.out.println(panelCoordsToEditorCoords(getPanelCoordinates(e)));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	boolean middleClickHeld = false;
	
	@Override
	public void mousePressed(MouseEvent e)
	{	
		lastDraggedLocation = getPanelCoordinates(e);
		middleClickHeld = e.getButton() == 2 ? true : false;
		
		for (WireJunction j : WireJunction.getAllWireJunctions(circuit))
		{
			System.out.println("JUNCTION @ " + j.getLocation());
		}
		
		for (Wire w : circuit.getAllWires())
		{
			System.out.println(w);;

		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		middleClickHeld = e.getButton() == 2 ? false : true;
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
		prefWidth = getWidth();
		prefHeight = getHeight();
		repaint();
		// TODO Auto-generated method stub
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub	
	}
}
