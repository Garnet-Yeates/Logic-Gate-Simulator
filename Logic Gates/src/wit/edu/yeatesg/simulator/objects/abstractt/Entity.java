package wit.edu.yeatesg.simulator.objects.abstractt;
import java.awt.Graphics;
import java.util.ArrayList;

import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.Rectangle;
import wit.edu.yeatesg.simulator.objects.math.Vector;
import wit.edu.yeatesg.simulator.objects.other.Circuit;

public abstract class Entity
{		
	protected BigPoint location;
	
	protected Circuit circuit;
	
	protected Entity parentEntity;
	protected Entity[] childEntities;
	
	protected Rectangle bounds;
	protected Rectangle hoverBounds;
	
	protected boolean movable;
	
	public abstract void draw(Graphics g);
	
	/**
	 * Obtains the location of the origin point of this entity
	 * @return a {@link BigPoint} that represents the location of this Entity
	 */
	public BigPoint getLocation()
	{
		return location;
	}
	
	// TODO remember that im going to put a "Entity selected" and "Entity hovered" field in the main GUI
	// part of this program. And I need to remember that when something is hovered that it overrides
	// selecting anything else (aka when you click and something that is within its parents hitbox is
	// hovered, it selects the hovered component and not the whole parent component
	
	/**
	 * This represents the area around the origin point of this Entity that is sensitive
	 * to clicks. If the user clicks anywhere in this Rectangle, then this component
	 * should be selected
	 * @return the area around this entity that is sensitive to clicks
	 */
	public Rectangle getBounds()
	{
		return bounds.copy();
	}
	
	/**
	 * This represents the area around the origin point of this Entity that is sensitive
	 * to the mouse hovering over it. If the user moves the mouse anywhere in this Rectangle,
	 * then this component should be hovered
	 * @return the area around this entity that is sensitive to mouse hovering
	 */
	public Rectangle getHoverBounds()
	{
		return hoverBounds.copy();
	}
	
	/**
	 * Determines whether or not this entity has boundaries that are sensitive to mouse hovering
	 * @return whether or not this entity has hover bounds
	 */
	public boolean hasHoverBounds()
	{
		return hoverBounds != null;
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
		circuit.remove(this);
		onDelete();
	}
	
	public abstract void onDelete();
	
}
