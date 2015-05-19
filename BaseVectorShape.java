package astronutty;
import java.awt.Shape;

public class BaseVectorShape {

	//class variables
	private Shape shape;
	private boolean alive;
	private double x , y;
	private double velX, velY;
	private double moveAngle, faceAngle;
	
	//accessor methods
	public Shape getShape(){return shape;}
	public boolean isAlive(){return alive;}
	public double getX(){return x;}
	public double getY(){return y;}
	public double getVelX(){return velX;}
	public double getVelY(){return velY;}
	public double getMoveAngle(){return moveAngle;}
	public double getFaceAngle(){return faceAngle;}
	
	//our mutator and helper methods
	public void setShape(Shape shape){this.shape = shape;}
	public void setAlive(boolean alive){this.alive = alive;}
	public void setX(double x){this.x = x;}
	public void incX(double i){this.x += i;}
	public void setY(double y){this.y = y;}
	public void incY(double j){this.y += j;}
	public void setVelX(double velX){this.velX = velX;}
	public void incVelX(double h){this.velX += h;}
	public void setVelY(double velY){this.velY = velY;}
	public void incVelY(double k){this.velY += k;}
	public void setFaceAngle(double angle){this.faceAngle = angle;}
	public void incFaceAngle(double c){this.faceAngle += c;}
	public void setMoveAngle(double angle){this.moveAngle = angle;}
	public void incMoveAngle(double e){this.moveAngle += e;}
	
	BaseVectorShape(){
		setShape(null);
		setAlive(false);
		setX(0.0);
		setY(0.0);
		setVelX(0.0);
		setVelY(0.0);
		setMoveAngle(0.0);
		setFaceAngle(0.0);
	}
}
