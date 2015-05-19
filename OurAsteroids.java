package astronutty;
import java.awt.Polygon;
import java.awt.Rectangle;

public class OurAsteroids extends BaseVectorShape{

	//defining our polygonal shape for the asteroids
	private int[] asteroidX = {-20, -13, 0, 22, 20, 12, 2, -10, -22, -16};
	private int[] asteroidY = {20, 23, 17, 20, 16, -20, -22, -14, -17, -20, -5};
	
	//setting the rotational speed of the asteroids
	protected double rotationVelocity;
	public double getRotationVelocity(){return rotationVelocity;}
	public void setRotationVelocity(double z){rotationVelocity = z;}
	
	//the bounding rectangle for our asteroids
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle((int)getX() - 20, (int)getY() - 20, 40, 40);
		return r;
	}
	
	//the constructor for asteroids
	OurAsteroids(){
		setShape(new Polygon(asteroidX, asteroidY, asteroidX.length));
		setAlive(true);
		setRotationVelocity(0.0);
	}
}
