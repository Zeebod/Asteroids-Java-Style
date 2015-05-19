/*
 * Thanks to Mr. Johnathan S. Harbour and his guidance on Java Game Programming.
 * Updated this game to work and run with the current version of Java. This program
 * will be used entirely for academic purposes and nothing else. Any attempt to use
 * this game in a fashion to generate monetary gain is in breach of copyright law.
 */



package astronutty;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

//This is the primary class for the game
public class AstroNutty extends Applet implements Runnable, KeyListener{

	//Game Specific variables, all have DEFAULT access type applied
	//The main thread becomes the game loop
	Thread gameLoop;
	
	//use this as a double buffer to prevent screen flickering
	BufferedImage backBuffer;
	
	//the main drawing object for the back buffer
	Graphics2D g2d;
	
	//toggle for drawing bounding boxes
	boolean showBounds = false;
	
	//create the asteroids array
	int ASTEROIDS = 20;
	OurAsteroids[] ast = new OurAsteroids[ASTEROIDS];
	
	//create the bullet array
	int BULLETS = 10;
	Bullets[] bullet = new Bullets[BULLETS];
	int currentBullet = 0;
	
	//the player's ship
	StarShip ship = new StarShip();
	
	//create the identity transform (0,0)
	AffineTransform identity = new AffineTransform();
	
	//create a random number generator
	Random rand = new Random();
	
	//this is a new method that involves the applet init() event. This event is designed
	//for the first time the applet starts, the double buffer is created, where all graphics
	//for the game will be created, remember the double buffer is designed to allow the
	//screen to render smooth images without flickering.
	public void init(){
		//create the back buffer for smoothe graphics
		backBuffer = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
		
		//set up the ship
		ship.setX(320);
		ship.setY(240);
		
		//set up the bullets
		for(int n = 0; n < BULLETS; n++){
			bullet[n] = new Bullets();
		}
		
		//create the asteroids
		for(int n = 0; n < ASTEROIDS; n++){
			ast[n] = new OurAsteroids();
			ast[n].setRotationVelocity(rand.nextInt(3) + 1);
			ast[n].setX((double)rand.nextInt(600) + 20);
			ast[n].setY((double)rand.nextInt(440) + 20);
			ast[n].setMoveAngle(rand.nextInt(360));
			double ang = ast[n].getMoveAngle() - 90;
			ast[n].setVelX(calcAngleMoveX(ang));
			ast[n].setVelY(calcAngleMoveY(ang));
		}
		
		//star the user input listener
		addKeyListener(this);
	}
	
	//applet update event to redraw the screen
	public void update(Graphics g){
		//start off transforms as indentity
		g2d.setTransform(identity);
		
		//erase the background
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width,	getSize().height);
		
		//print some status information
		g2d.setColor(Color.WHITE);
		g2d.drawString("Ship: " + Math.round(ship.getX()) + " , " + Math.round(ship.getY()), 5, 10);
		g2d.drawString("Move angle: " + Math.round(ship.getMoveAngle()) + 90, 5, 25);
		g2d.drawString("Face angle: " + Math.round(ship.getFaceAngle()), 5, 40);
		
		//draw the game grpahics
		drawShip();
		drawBullets();
		drawAsteroids();
		
		//repaint the applet window
		paint(g);
	}
	
	
	//drawShip called by applet update event
	public void drawShip(){
		g2d.setTransform(identity);
		g2d.translate(ship.getX(), ship.getY());
		g2d.rotate(Math.toRadians(ship.getFaceAngle()));
		g2d.setColor(Color.CYAN);
		g2d.fill(ship.getShape());
	}
	
	//drawing the bullets called in update event
	public void drawBullets(){
		//iterate through the array of bullets
		for(int i = 0; i < BULLETS; i++){
			//is this bullet currently alive? we need to check
			if(bullet[i].isAlive()){
				//draw the bullet
				g2d.setTransform(identity);
				g2d.translate(bullet[i].getX(), bullet[i].getY());
				g2d.setColor(Color.GREEN);
				g2d.draw(bullet[i].getShape());
			}
		}
	}
	
	//now we draw our asteroids that were called in update
	public void drawAsteroids(){
		//iterate through the asteroids array
		for(int i = 0; i < ASTEROIDS; i++){
			//is this asteroid being used?
			if(ast[i].isAlive()){
				//draw it if it is being used
				g2d.setTransform(identity);
				g2d.translate(ast[i].getX(), ast[i].getY());
				g2d.rotate(Math.toRadians(ast[i].getMoveAngle()));
				g2d.setColor(Color.DARK_GRAY);
				g2d.fill(ast[i].getShape());
			}
		}
	}
	
	//how we refresh the screen, which is handled in this paint method,
	//which is once again called in update
	//applet window repaint event -- draw the back buffer
	public void paint(Graphics g){
		//draw the back buffer onto the applet window
		g.drawImage(backBuffer, 0, 0, this);
	}
	
	
	//thread events and our game loop
	//here we are working with the Runnable interface,
	//which tells our applet we are working with more than one thread
	//a thread is sort of a mini program that can run on its own
	//we can create a new thread in the start() event, and then destroy
	//it later on in stop(). The most interesting is the run() event, which
	//will contain the gameUpdate() within a while, that will more or less
	//keep the game running based upon checking for collisions and moving 
	//game objects around on the screen.
	
	//thread start event -- this starts the game loop
	public void start(){
		//create the gameloop thread for real-time updates
		gameLoop = new Thread(this);
		gameLoop.start();
	}
	
	//the thread run event (essentially our game loop)
	public void run() {
		// TODO Auto-generated method stub
		//acquire the current thread
		Thread t = Thread.currentThread();
			
		//keep going as long as the thread is alive
		while(t == gameLoop){
			try{
				//update the game Loop
				gameUpdate();
					
				//target framerate is 50 fps
				Thread.sleep(20);
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
			repaint();
		}
	}
	
	//garbage collection and our stop thread event
	public void stop(){
		//kill the game loop thread
		gameLoop = null;
	}
	
	//gameUpdate -- this is called by the applet when it is time to update
	//the window refresh
	//move and animate the object in the game
	private void gameUpdate(){
		updateShip();
		updateBullets();
		updateAsteroids();
		checkCollisions();
	}
	
	//updateShip()
	//this method updates the ships x and y coordinates using velocity
	//variables. This will also keep our ship locked to the screen in a sense...
	//when a ship reaches the edge of a screen it will be repositioned to the 
	//other side of the screen
	//update the ship position based on velocity
	public void updateShip(){
		//update the ship's X position
		ship.incX(ship.getVelX());
		
		//wrap around left/right
		if(ship.getX() < -10){
			ship.setX(getSize().width + 10);
		}else if(ship.getX() > getSize().width + 10){
			ship.setX(-10);
		}
		
		//update the ship's position
		ship.incY(ship.getVelY());
		
		//wrap around top/bottom
		if(ship.getY() < -10){
			ship.setY(getSize().height + 10);
		}else if(ship.getY() > getSize().height + 10){
			ship.setY(-10);
		}
		
	}
	
	
	//updating the bullets
	//the bullets are updated based on velocity similar to the ship
	public void updateBullets(){
		//move each of the bullets
		for(int i = 0; i < BULLETS; i++){
			//is this bullet being used?
			if(bullet[i].isAlive()){
				//update x position
				bullet[i].incX(bullet[i].getVelX());
				
				//bullet disappears at left/right edge
				//set the value to false, and dispose of the bullet
				if(bullet[i].getX() < 0 || bullet[i].getX() > getSize().width){
					bullet[i].setAlive(false);
				}
				
				//update the y position
				bullet[i].incY(bullet[i].getVelY());
				
				//bullet disappears at top/bottom edge
				//set the value to false, and dipose of the bullet
				if(bullet[i].getY() < 0 || bullet[i].getY() > getSize().width){
					bullet[i].setAlive(false);
				}
			}
		}
	}
	
	//Update the asteroids based on velocity
	public void updateAsteroids(){
		//move and rotate the asteroids
		for(int i = 0; i < ASTEROIDS; i++){
			//is this asteroid being used?
			if(ast[i].isAlive()){
				//update the asteroids x position
				ast[i].incX(ast[i].getVelX());
				
				//warp the asteroid at screen edges
				if(ast[i].getX() < -20){
					ast[i].setX(getSize().width + 20);
				}else if(ast[i].getX() > getSize().width + 20){
					ast[i].setX(-20);
				}
				
				//update the asteroid's Y value
				ast[i].incY(ast[i].getVelY());
				
				if(ast[i].getY() < -20){
					ast[i].setY(getSize().height + 20);
				}else if(ast[i].getY() > getSize().height + 20){
					ast[i].setY(-20);
				}
				
				//update the asteroid's rotation
				ast[i].incMoveAngle(ast[i].getRotationVelocity());
				
				//keep the angle within 0 - 359 degrees
				if(ast[i].getMoveAngle() < 0){
					ast[i].setMoveAngle(360 - ast[i].getRotationVelocity());
				}else if(ast[i].getMoveAngle() > 360){
					ast[i].setMoveAngle(ast[i].getRotationVelocity());
				}
			}
		}
	}
	
	//testing for collisions
	//Test asteroids for collisions with bullets and the ship
	public void checkCollisions(){
		//iterate through the asteroids array
		for(int r = 0; r < ASTEROIDS; r++){
			//is this asteroid being used?
			if(ast[r].isAlive()){
				//check for collisions with bullets first
				for(int c = 0; c < BULLETS; c++){
					if(bullet[c].isAlive()){
						//perform the collision test
						if(ast[r].getBounds().contains(bullet[c].getX(), bullet[c].getY())){
							bullet[c].setAlive(false);
							ast[r].setAlive(false);
							continue;
						}
					}
				}
				
				//check for collision with ship
				if(ast[r].getBounds().intersects(ship.getBounds())){
					ast[r].setAlive(false);
					ship.setX(320);
					ship.setY(240);
					ship.setFaceAngle(0);
					ship.setVelX(0);
					ship.setVelY(0);
					continue;
				}
			}
		}
	}
	
	public double calcAngleMoveX(double angle){
		return (double)(Math.cos(angle*Math.PI/180));
	}
	
	public double calcAngleMoveY(double angle){
		return (double)(Math.sin(angle*Math.PI/180));
	}

	//this is the only key listener even that the game uses
	public void keyPressed(KeyEvent k) {
		int keyCode = k.getKeyCode();
		switch(keyCode){
		case KeyEvent.VK_LEFT:
			//left arror rotates ship left 5 degrees
			ship.incFaceAngle(-5);
			if(ship.getFaceAngle() < 0) ship.setFaceAngle(360 - 5);
			break;
		case KeyEvent.VK_RIGHT:
			//right arrow rotates ship right 5 degrees
			ship.incFaceAngle(5);
			if(ship.getFaceAngle() > 360) ship.setFaceAngle(5);
			break;
		case KeyEvent.VK_UP:
			//up arrow adds thrust to the ship (1/10 normal speed)
			ship.setMoveAngle(ship.getFaceAngle() - 90);
			ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * 0.1);
			ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * 0.1);
			break;
		//Ctrl, Enter, or space can be used to fire weapon
		case KeyEvent.VK_SPACE:
			//fire a bullet
			currentBullet++;
			if(currentBullet > BULLETS - 1) currentBullet = 0;
			bullet[currentBullet].setAlive(true);
			
			//point bullet in same direction ship is facing
			bullet[currentBullet].setX(ship.getX());
			bullet[currentBullet].setY(ship.getY());
			bullet[currentBullet].setMoveAngle(ship.getFaceAngle() - 90);
			
			//fire bullet at angle of the ship
			double angle = bullet[currentBullet].getMoveAngle();
			double svx = ship.getVelX();
			double svy = ship.getVelY();
			bullet[currentBullet].setVelX(svx + calcAngleMoveX(angle) * 2);
			bullet[currentBullet].setVelY(svy + calcAngleMoveY(angle) * 2);
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent k) {
		// TODO Auto-generated method stub
		
	}


}
