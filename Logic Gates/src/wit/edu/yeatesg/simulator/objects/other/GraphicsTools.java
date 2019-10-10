package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Color;
import java.awt.Graphics;

import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;

public class GraphicsTools
{
	public static void drawVerticalLine(BigPoint p1, BigPoint p2, int extraThickness, Graphics g, Circuit circuit)
	{
		if (p2.y < p1.y)
		{
			BigPoint temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		LittlePoint topLoc = LittlePoint.getPanelCoords(p1, circuit);
		LittlePoint bottomLoc = LittlePoint.getPanelCoords(p2, circuit);

		int xPos = topLoc.x + circuit.getGridPointDrawOffset() - extraThickness;
		int yExtender = circuit.getGridPointDrawOffset() + - extraThickness;
		for (int i = 0; i < circuit.getGridPointDrawSize() + extraThickness*2; i++, xPos++)
			g.drawLine(xPos, topLoc.y + yExtender, xPos, bottomLoc.y - yExtender);
	}

	public static void drawHorizontalLine(BigPoint p1, BigPoint p2, int extraThickness, Graphics g, Circuit circuit)
	{
		if (p2.x < p1.x)
		{
			BigPoint temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		LittlePoint leftLoc = LittlePoint.fromBigPoint(p1, circuit);
		LittlePoint rightLoc = LittlePoint.fromBigPoint(p2, circuit);
	
		leftLoc = LittlePoint.getPanelCoords(leftLoc, circuit);
		rightLoc = LittlePoint.getPanelCoords(rightLoc, circuit);
	
		int yPos = leftLoc.y + circuit.getGridPointDrawOffset() - extraThickness;
		int xExtender = circuit.getGridPointDrawOffset() + - extraThickness;
		for (int i = 0; i < circuit.getGridPointDrawSize() + extraThickness*2; i++, yPos++)
			g.drawLine(leftLoc.x + xExtender, yPos, rightLoc.x - xExtender, yPos);
		
	}
	
	public static void drawSelectionCorners(BigPoint p, Color outer, Color inner, int size, Graphics g, Circuit circuit)
	{
		LittlePoint drawLoc = LittlePoint.getPanelCoords(p, circuit);
		int offset = size / -2;
		g.setColor(outer);
		g.fillRect(drawLoc.x + offset, drawLoc.y + offset, size, size);
		size -= 2;
		offset += 1;
		g.setColor(inner);
		g.fillRect(drawLoc.x + offset, drawLoc.y + offset, size, size);
	}
}
