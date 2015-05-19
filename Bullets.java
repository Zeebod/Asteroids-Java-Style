package astronutty;
import java.awt.*;
import java.awt.Rectangle;

public class Bullets extends BaseVectorShape{

	//bounding rectangle
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle((int)getX(), (int)getY(), 1, 1);
		return r;
	}
	
	Bullets(){
		//create the bullets shape
		setShape(new Rectangle(0,0,1,1));
		//make sure bullets are not created and fired until 
		//commanded to do so.
		setAlive(false);
	}
}
