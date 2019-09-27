package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Color;
import java.awt.Graphics;

import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;

public class GraphicsTools
{
	private EditorPanel panel;
	private Graphics g;
	
	public GraphicsTools(EditorPanel panel, Graphics g)
	{
		this.panel = panel;
		this.g = g.create();
	}
	
	public void drawVerticalLine(BigPoint p1, BigPoint p2, Graphics g, int extraThickness)
	{
		if (p2.y < p1.y)
		{
			BigPoint temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		LittlePoint topLoc = panel.fromBigPoint(p1);
		LittlePoint bottomLoc = panel.fromBigPoint(p2);
	
		topLoc = panel.editorCoordsToPanelCoords(topLoc);
		bottomLoc = panel.editorCoordsToPanelCoords(bottomLoc);
	
		int xPos = topLoc.x + panel.getCircuit().getGridPointDrawOffset().x - extraThickness;
		int yExtender = panel.getCircuit().getGridPointDrawOffset().y + - extraThickness;
		for (int i = 0; i < panel.getCircuit().getGridPointDrawSize() + extraThickness*2; i++, xPos++)
		{
			g.setColor(Color.GREEN);
			g.drawLine(xPos, topLoc.y + yExtender, xPos, bottomLoc.y - yExtender);
		}
	}

	public void drawHorizontalLine(BigPoint p1, BigPoint p2, Graphics g, int extraThickness)
	{
		if (p2.x < p1.x)
		{
			BigPoint temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		LittlePoint leftLoc = panel.fromBigPoint(p1);
		LittlePoint rightLoc = panel.fromBigPoint(p2);
	
		leftLoc = panel.editorCoordsToPanelCoords(leftLoc);
		rightLoc = panel.editorCoordsToPanelCoords(rightLoc);
	
		int yPos = leftLoc.y + panel.getCircuit().getGridPointDrawOffset().y - extraThickness;
		int xExtender = panel.getCircuit().getGridPointDrawOffset().x + - extraThickness;
		for (int i = 0; i < panel.getCircuit().getGridPointDrawSize() + extraThickness*2; i++, yPos++)
		{
			g.setColor(Color.GREEN);
			g.drawLine(leftLoc.x + xExtender, yPos, rightLoc.x - xExtender, yPos);
		}
	}

}
