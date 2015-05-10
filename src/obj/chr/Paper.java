package obj.chr;

import obj.env.Floor;
import obj.env.FloorBlock;
import obj.env.Wall;
import obj.env.blk.GroundBlock;
import obj.itm.Coin;
import obj.prim.Physical;
import obj.prt.Dust;
import rm.RoomController;
import func.Math2D;
import gfx.GOGL;
import gfx.Shape;

public abstract class Paper extends Physical {
	protected boolean isSpinning = false;
	
	//FLIP VARIABLES
		private final static float FLIP_SPEED = .1f;
		protected float flipDir = 1;
		private int vDir = 1;
		protected int dir = 1, height = 16;
	protected boolean isMoving = false, isSpeaking = false, canCollide = true;
	
	
	
	//Air Variables
	protected float inAirDir, inAirDir2, inAirSpeed, inAirSpeed2;
	
	protected final static int A_NOTHING = -1, A_SPEAKING = 0, A_WAIT = 1, A_TOPOINT = 2, A_HOLD = 3, A_HAMMER = 4, A_TOHELPER = 5;
	protected int action = A_NOTHING;
	
	//GO TO VARIABLES
	float goToX, goToY, goToZ, goToVel;
	String goToScript;
	
	
	
	
	
	public Paper(float x, float y, float z) {
		super(x, y, z);
		
		size = 4;
		
		//animated_model_ini()
		//animated_col_model_ini()
		
		shape = Shape.createShadow("Player", 0,0,0);
	}
	
	//PARENT FUNCTIONS
		public void update(float deltaT) {
			super.update(deltaT);
			
			if(!isItem()) {
				updateFlip();
				//modelUpdate();
				
				//if(action != "")
				//actionUpdate();
				
				//pipeMotionUpdate();
			}
			
			//updateFloor();
						
			//timer = increment_timer(timer, -1, -.5);
			
			
			boolean notInPipe = false;
			/*if(isPlayer())
					if(enterPipe == 0 || enterPipe == 3)
							notInPipe = true;
			else
				notInPipe = true;
			*/
			
			if(notInPipe) {
				//motionSlope();
				//motionFloor();
			}
				
				
			updateShadow();
		}
		
	public boolean collide(Physical other) {
		/*Floor.collideAll(this);
		Wall.collideAll(this);
		Coin.collideAll(this);
		GroundBlock.collideAll(this);*/
		
		return false;
	}
	
	private void updateFlip() {
	    float camDir, myDir, turnAmount;
	    camDir = GOGL.getCamDir();
	    //if(inAir && speed > 0)
	    //    myDir = point_direction(xprevious,yprevious,x,y);
	    //else
	        myDir = getDirection();
	    turnAmount = Math2D.turnToDirection(myDir,camDir);
	    if(turnAmount > .1)
	        dir = -1;
	    if(turnAmount < -.1)
	        dir = 1;
	    
	    vDir = calcVDir();
		
		if(!isSpinning) {
			float n;
		    if(RoomController.isInBattle())
		        n = 1.6f;
		    else
		        n = 1;
	
		    if(dir == 1 && flipDir < 1)
		        flipDir += n*FLIP_SPEED;
		    if(dir == -1 && flipDir > -1)
		        flipDir -= n*FLIP_SPEED;
		    flipDir = Math2D.contain(-1,flipDir,1);
		}
	}
	
	public void centerCamera(float camDis, float camDir, float camZDir) {
		float cX, cY, cZ, camX, camY, camZ;
		cX = x;
		cY = y;
		cZ = floorZ+32;
		camX = cX + Math2D.calcPolarX(camDis, camDir+180, camZDir);
		camY = cY + Math2D.calcPolarY(camDis, camDir+180, camZDir);
		camZ = cZ + Math2D.calcPolarZ(camDis, camDir, camZDir+180);
		
		//Graphics3D.setProjection(20, 20, 0, 0, 0, 0);
		GOGL.setProjectionPrep(camX, camY, camZ,  cX, cY, cZ);
	}
	
	
	
	
	//DRAWING FUNCTIONS
	private int calcVDir() {
		if(Math.abs(Math2D.calcAngDiff(getDirection(), GOGL.getCamDir())) < 90)
			return 1;
		else 
			return -1;
	}
	
	public int getVDir() {
		return vDir;
	}
	
	//ACCESSOR AND MUTATOR FUNCTIONS
		public void updateShadow() {
			float grZ, size;
			
			grZ = Floor.findGroundZ(x, y, z);
			size = (float) ((1 - .01*Math.abs(z - grZ))*1.2*8);
	
			//Move Shadow to Floor
			shape.setShadowPosition(grZ+.3f);
		}
		
		public float getHeight() {
			return height;
		}
}
