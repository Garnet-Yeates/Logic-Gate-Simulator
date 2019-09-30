package wit.edu.yeatesg.simulator.objects.abstractt;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Rectangle;
import wit.edu.yeatesg.simulator.objects.math.Shape;
import wit.edu.yeatesg.simulator.objects.math.Vector;
import wit.edu.yeatesg.simulator.objects.other.Circuit;
import wit.edu.yeatesg.simulator.objects.other.GraphicsTools;

public abstract class Entity
{		
	protected BigPoint location;
	
	protected Circuit circuit;
	
	protected Entity parentEntity;
	protected Entity[] childEntities;
		
	protected boolean movable;
	
	public abstract void draw(Graphics g);
	
	public void drawSelectionIndicator(Graphics g)
	{
		Shape shape = getSelectionBounds();
		for (BigPoint p : shape.getAllPoints())
		{
			GraphicsTools.drawSelectionCorners(p, Color.BLACK, new Color(255, 242, 204), circuit.getGapBetweenPoints() / 2, g, circuit);
		}
	}
	
	public abstract Shape getSelectionBounds();
	
	public boolean withinDrawingBounds()
	{
		return withinDrawingBounds(location, circuit);
	}	
	
	public static boolean withinDrawingBounds(BigPoint loc, Circuit c)
	{
		LittlePoint panelCoords = LittlePoint.getPanelCoords(loc, c);
		int width = c.getEditorPanel().getWidth();
		int height = c.getEditorPanel().getHeight();
		int x = panelCoords.x;
		int y = panelCoords.y;
		if (x > 0 - width*0.05 && x < width + width*0.05 && y > 0 - height*0.05 && y < height + height*0.05)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Obtains the location of the origin point of this entity
	 * @return a {@link BigPoint} that represents the location of this Entity
	 */
	public BigPoint getLocation()
	{
		return location;
	}

	/**
	 * Determines whether or not this Entity is attached to a parent entity
	 * @return true if this Entity has a parent entity
	 */
	public boolean hasParentEntity()
	{
		return parentEntity != null;
	}
	
	/**
	 * Determines whether or not this Entity has child entities attached to it
	 * @return true if this Entity has child entities
	 */
	public boolean hasChildEntities()
	{
		return childEntities != null;
	}
	
	/**
	 * Determines whether or not this Entity can be moved
	 * @return true if this entity can be moved
	 */
	public boolean isMovable()
	{
		return movable;
	}
	
	/**
	 * Method for when a movement is attempted on an entity. This method should be called whenever
	 * the user clicks and drags an Entity on the screen and tries to manually move it. If the
	 * program detects that this Entity has a parent entity, then this move will be cancelled but
	 * it will be attempted on the parent of this component. Once the highest parent is found, and
	 * the {@link #preMove(Vector)} check for the parent returns true, then {@link #forceMove(Vector)}
	 * will be called on that parent and all of its children.
	 * @see #forceMove(Vector)
	 * @see #preMove(Vector)
	 * @param v the vector that will determine which direction this entity is moved in
	 */
	public void attemptMove(Vector v)
	{
		if (movable)
		{
			if (hasParentEntity())
			{
				parentEntity.attemptMove(v);
			} 
			else if (preMove(v))
			{
				forceMove(v);
			}
		}
	}
	
	/**
	 * This method is used to check if a movement in direction of the given vector is possible for
	 * this entity. For example of one entity shouldn't be overlapping another for whatever reason,
	 * then those entities should override this method and return false if they are overlapping
	 * each other. When {@link #attemptMove(Vector)} is called, this method is also called and the
	 * move will only be made if this method returns true
	 * @return true if a movement in the direction of v is possible for this entity
	 */
	protected boolean preMove(Vector v)
	{
		return true;
	}
	
	/**
	 * This method changes the location of this Entity based on the
	 * supplied vector
	 * @param v the vector that will be used to change this Entity's location
	 */
	private void forceMove(Vector v)
	{
		if (hasChildEntities())
		{
			for (Entity e : childEntities)
			{
				e.forceMove(v);
			}
		}
	}
	
	public void delete()
	{
		circuit.removeEntity(this);
		onDelete();
	}
	

	public abstract boolean intercepts(BigPoint p);
	
	public abstract void onDelete();
}
