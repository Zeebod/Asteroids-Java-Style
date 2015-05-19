/*
 * The starship class handles the shape, position, and velocity of the player's starship.
 * It includes its own bounding rectangle, which is calculated based on a custom polygon
 * shape for the starship. The starship class inherits all thep public properties and methods
 * from the BaseVectorShape class
 */

package astronutty;
import java.awt.Polygon;
import java.awt.Rectangle;

public class StarShip extends BaseVectorShape{
	
	//this is where we define the polygon shape
	private int[] shipx = {-6, -3, 0, 3, 6, 0};
	private int[] shipy = {6, 7, 7, 7, 6, -7};
	
	//bounding rectangle
	//this will draw the rectangle by manually filling in the bounds
	//if you research the definition you will find we can perform
	//this in a few different ways than the one we have here
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle((int)getX() - 6, (int)getY() - 6, 12, 12);
		return r;
	}
	
	StarShip(){
		setShape(new Polygon(shipx, shipy, shipx.length));
		setAlive(true);
	}

}
