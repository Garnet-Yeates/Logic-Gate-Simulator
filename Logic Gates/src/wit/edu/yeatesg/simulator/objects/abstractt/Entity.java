package wit.edu.yeatesg.simulator.objects.abstractt;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import wit.edu.yeatesg.simulator.objects.math.BigPoint;
import wit.edu.yeatesg.simulator.objects.math.LittlePoint;
import wit.edu.yeatesg.simulator.objects.math.Shape;
import wit.edu.yeatesg.simulator.objects.math.Vector;
import wit.edu.yeatesg.simulator.objects.other.Circuit;
import wit.edu.yeatesg.simulator.objects.other.GraphicsTools;

public abstract class Entity
{		
	protected BigPoint location;
		
	protected Circuit circuit;
	
	protected Entity parentEntity = null;
	protected ArrayList<Entity> childEntities = new ArrayList<Entity>();

	public void checkInterferingEntity()
	{
		for (Entity e : circuit.getAllEntities())
		{
			if (!childEntities.contains(e) && e != this && e.location != null && e.location == location)
			{
				throw new RuntimeException(this + " location interferes with " + e);
			}
		}
	}
	
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
		return childEntities != null && childEntities.size() > 0;
	}
	
	public void addChildEntity(Entity e)
	{
		if (!e.hasParentEntity())
		{
			childEntities.add(e);
			e.parentEntity = this;
			return;
		}
		throw new RuntimeException("Entity already has a parent!");
	}

	public ArrayList<Entity> getChildEntities()
	{
		return childEntities;
	}

	/**
	 * Determines whether or not this Entity can be moved
	 * @return true if this entity can be moved
	 */
	public abstract boolean isMovable();
	
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
		if (isMovable())
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
	
	public boolean isPokable()
	{
		return false;
	}
	
	public void onPoke() { }
	
	public abstract boolean intercepts(BigPoint p);
	
	public abstract void onDelete();

	protected int rotation;

	public void setRotation(int r)
	{
		if (r == 0 || r == 90 || r == 180 || r == 270)
		{
			rotation = r;
		}
		else throw new RuntimeException("Invalid Rotation!");
	}
	
	public int getRotation()
	{
		return rotation;
	}	
	
	/**
	 * This method should return the result of one of the other methods. For example
	 * an InputBlock would have its default rotation on 270 degrees so that its connection
	 * node will be on the right side of it. In that case, this should return {@link #determine270PointSet()}
	 * @return the set of points for this to be on by default.
	 */
	public abstract List<BigPoint> getDefaultPointSet();
	
	/**
	 * Returns the set of points for when this Entity is not rotated. The origin is the 0th index
	 * @return
	 */
	public abstract List<BigPoint> determine0PointSet();
	
	public List<BigPoint> getCurrentPointSet()
	{
		switch (rotation)
		{
		case 0:
			return get0PointSet();
		case 90:
			return get90PointSet();
		case 180:
			return get180PointSet();
		case 270:
			return get270PointSet();
		default:
			return null;
		}
	}
	
	public boolean hasPointSet()
	{
		return determine0PointSet() != null && determine0PointSet().size() > 1;
	}
	
	public BigPoint getOrigin()
	{
		if (hasPointSet())
			return determine0PointSet().get(0).clone();
		return null;
	}
	
	// 0   degrees <x, y> = <x, y>
	// 180 degrees <x, y> = <-y, -x>
	private List<BigPoint> determine180PointSet()
	{
		if (hasPointSet())
		{
			List<BigPoint> zeroDrawPoints = determine0PointSet();
			List<Vector> vecs = Entity.getOffsetsFromList(determine0PointSet());
			for (Vector v : vecs)
			{
				int x = v.x;
				v.x = v.y;
				v.y = x;
				v.x *= -1;
				v.y *= -1;
			}
			return Entity.getPointsFromOffsetList(zeroDrawPoints.get(0), vecs);
		}
		return null;
	}

	// 0   degrees <x, y> = <x, y>
	// 270 degrees <x, y> = <y, -x>
	private List<BigPoint> determine270PointSet()
	{
		if (hasPointSet())
		{
			List<BigPoint> zeroDrawPoints = determine0PointSet();
			List<Vector> vecs = Entity.getOffsetsFromList(determine0PointSet());
			for (Vector v : vecs)
			{
				int x = v.x;
				v.x = v.y;
				v.y = x;
				v.y *= -1;
			}
			return Entity.getPointsFromOffsetList(zeroDrawPoints.get(0), vecs);
		}
		return null;
	}
	
	private List<BigPoint> p0;
	private List<BigPoint> p90;
	private List<BigPoint> p180;
	private List<BigPoint> p270;

	public List<BigPoint> get0PointSet()
	{
		if (p0 == null) p0 = determine0PointSet();
		return p0;
	}
	
	public List<BigPoint> get90PointSet()
	{
		if (p90 == null && hasPointSet()) p90 = determine90PointSet();
		return p90;
	}
	
	public List<BigPoint> get180PointSet()
	{
		if (p180 == null && hasPointSet()) p180 = determine180PointSet();
		return p180;
	}
	
	public List<BigPoint> get270PointSet()
	{
		if (p270 == null && hasPointSet()) p270 = determine270PointSet();
		return p270;
	}

	// 0   degrees <x, y> = <x, y>
	// 90  degrees <x, y> = <-y, x>
	private List<BigPoint> determine90PointSet()
	{
		if (hasPointSet())
		{
			List<BigPoint> zeroDrawPoints = determine0PointSet();
			List<Vector> vecs = Entity.getOffsetsFromList(determine0PointSet());
			System.out.println(zeroDrawPoints);
			System.out.println(vecs);
			for (Vector v : vecs)
			{
				int x = v.x;
				v.x = v.y;
				v.y = x;
				v.x *= -1;
			}
			return Entity.getPointsFromOffsetList(zeroDrawPoints.get(0), vecs);
		}
		return null;
	}

	private static List<Vector> getOffsetsFromList(List<BigPoint> points)
	{
		if (points.size() >= 2)
		{
			int j = points.size() - 2;
			ArrayList<Vector> list = new ArrayList<>(points.size() - 1);
			for (int i = points.size() - 1; i < 1; i--, j--)
			{
				list.add(j, new Vector(points.get(i), points.get(i - 1)));
			}
			return list;
		}
		return null;
	}

	private static List<BigPoint> getPointsFromOffsetList(BigPoint origin, List<Vector> vecList)
	{
		ArrayList<BigPoint> list = new ArrayList<>(vecList.size() + 1);
		list.add(origin);
		for (int i = 1; i < list.size(); i++) list.add(i, origin.addVector(vecList.get(i - 1)));
		return list;
	}
}
