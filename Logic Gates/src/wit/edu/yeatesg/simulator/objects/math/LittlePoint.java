package wit.edu.yeatesg.simulator.objects.math;

import java.awt.event.MouseEvent;

import wit.edu.yeatesg.simulator.objects.other.Circuit;

public class LittlePoint
{	
	public int x;
	public int y;
	
	public LittlePoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void addOffset(Vector drawOffset)
	{
		this.x += drawOffset.x;
		this.y += drawOffset.y;
	}
	
	public void addOffset(int offset)
	{
		addOffset(new Vector(offset, offset));
	}
	
	@Override
	public String toString()
	{
		return "( " + x + " , " + y + " )";
	}
	
	private static LittlePoint clickOffset = new LittlePoint(-8, -31);
	
	public static LittlePoint getPanelCoordinates(MouseEvent e)
	{
		int x = (int) (e.getX() + clickOffset.x);
		int y = (int) (e.getY() + clickOffset.y);
		
		return new LittlePoint(x, y);
	}
	
	public static LittlePoint getEditorCoords(LittlePoint panelCoords, Circuit circuit)
	{
		int x = (int) (panelCoords.x - circuit.getOffset().x);
		int y = (int) (panelCoords.y - circuit.getOffset().y);
		
		return new LittlePoint(x, y);
	}
	
	public static LittlePoint getPanelCoords(BigPoint bigPoint, Circuit circuit)
	{
		return getPanelCoords(LittlePoint.fromBigPoint(bigPoint, circuit), circuit);
	}
	
	public static LittlePoint getEditorCoords(MouseEvent e, Circuit circuit)
	{
		return getEditorCoords(LittlePoint.getPanelCoordinates(e), circuit);
	}
	
	public static LittlePoint getPanelCoords(LittlePoint editorCoords, Circuit circuit)
	{
		int x = (int) (editorCoords.x + circuit.getOffset().x);
		int y = (int) (editorCoords.y + circuit.getOffset().y);
		
		return new LittlePoint(x, y);
	}
	
	public static LittlePoint fromBigPoint(BigPoint p, Circuit circuit)
	{
		return new LittlePoint((int) p.x * circuit.getGapBetweenPoints(),(int) p.y * circuit.getGapBetweenPoints());
	}

}
