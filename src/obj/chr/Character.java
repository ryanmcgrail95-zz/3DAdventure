package obj.chr;

import com.jogamp.opengl.util.texture.Texture;

import obj.prt.Dust;
import snd.SoundController;
import cont.CharacterController;
import func.Math2D;
import gfx.GOGL;
import gfx.Shape;
import gfx.SpriteMap;
import gfx.TextureExt;

public abstract class Character extends Paper {
	protected final static float IMAGE_SPEED = .8f;
	private float upZ = 0;
	protected boolean stepSound = false;
	
		
	private SpriteMap map;
	
	//Image Variables
		protected TextureExt image = null;
		protected float imageIndex = 0, imageNumber = 6;
	
	//Spinning Variables
		//protected boolean isSpinning = false;
		protected final static float SPIN_VEL = 3f; // 2.5
		protected float spinTimer = 0, spinDirection = 0;
		protected int[] spinSound = null;
		
	//CHARACTER VARIABLES
		protected String character;
	
	//JUMPING VARIABLES
		protected boolean isJumping = false;
		protected float jumpHold = 0;
	
	//FOLLOW VARIABLES
		private static final byte FOLLOW_NUM = 30;
		private float[] followXP = new float[FOLLOW_NUM],
				followYP = new float[FOLLOW_NUM],
				followFDP = new float[FOLLOW_NUM];
		private boolean[] followJP = new boolean[FOLLOW_NUM];
		
		
	public Character(float x, float y, float z, String characterType) {
		super(x, y, z);
		
		shape.addWall(-16, 0, 32, 16, 0, 0, null);//new Shape(Graphics3D.createWall(-16, 0, 31, 16, 0, -1, null));
		shape.setColor(255,0,0);
		
		character = characterType;
		
		map = CharacterController.getCharacter(characterType).getSpriteMap();
		
		followIni();
	}

	//PARENT FUNCTIONS
		public void update(float deltaT) {
			super.update(deltaT);
						
			followUpdate();
			
			containXYSpeed();
			modelUpdate();
			
			motionUpdateFloor(deltaT);
			
			
			updateSpin();
			
			shape.setPosition(x, y, z+upZ);
			if(image != null) {
				Texture tex = image.getFrame(imageIndex);
				//Texture tex = image.getFrameEffect(imageIndex, TextureExt.E_PIXELIZE);
				shape.setTexture(tex);
			}
		}
		
		public void followIni() {
			//Initializes the variables for an animated object, so other objects can follow it.	
		
			for(byte i = 0; i < FOLLOW_NUM; i += 1) {
			    followXP[i] = x;
			    followYP[i] = y;
			    followFDP[i] = flipDir;
			    followJP[i] = false;
			}
		}
	
		public boolean draw() {
			if(getVDir() == 1)
				shape.setRotation(0, 0, GOGL.getCamDir() + flipDir*90);
			else
				shape.setRotation(0, 0, GOGL.getCamDir() + 180 - flipDir*90);
			
			return true;
		}
		
	//UPDATE FUNTIONS
		public void followUpdate() {
			//Updates the follow variables.
		
			for(byte i = FOLLOW_NUM-1; i > 0; i -= 1) {
			    followXP[i] = followXP[i-1];
			    followYP[i] = followYP[i-1];
			    followFDP[i] = followFDP[i-1];
			    followJP[i] = followJP[i-1];
			}
		
			if(isSpinning)
			    followFDP[FOLLOW_NUM-1] = dir;
			    
			followXP[0] = x;
			followYP[0] = y;
			
			if(!isPlayer() ||  !isSpinning)
			    followFDP[0] = flipDir;
			followJP[0] = false;
		}
	
		
	//UNIQUE FUNCTIONS
		
	public void motionUpdateFloor(float deltaT) {
		//if(object_index == objNPC)
		//	    if(action == "talk")
		//	        exit;
		/*if(object_get_parent(object_index) == parHelper)
		    if(global.currentAction == "kooper_5")
		        exit;*/
		
		//var parent;
		//parent = object_get_parent(object_index);
		if(isPlayer()) {
		    if(true)//sprite != mario_hammer && sprite != mario_hammer_up && sprite != mario_eating && sprite != mario_finger && sprite != mario_adjustcap)
		    {
		        float addIndex;
		        addIndex = (float) (deltaT*1.05*(getXYSpeed()/MAX_SPEED)*IMAGE_SPEED*.9);
		        
		        /*if(keyboard_check(vk_shift))
		        {
		            float rndIndex;
		            rndIndex = round(imageIndex);
		            
		            if(rndIndex != 2 && rndIndex != 5)
		                addIndex *= .25;
		            else
		                addIndex *= 2;
		        }*/
		        
		        imageIndex += addIndex;
		    }
		    /*else {
		        if(sprite == mario_hammer || sprite == mario_hammer_up)
		            image_index += delta*1;
		        else
		            image_index += delta*.25;
		    }*/
		}
		else//object_get_parent(parent) == parNonP || parent == parHelper)
		    imageIndex += deltaT*.6f*IMAGE_SPEED;//.6
		
		if(isPlayer()) {
		    //if(curAction != "hammer")
		        if(imageIndex > imageNumber) {
		            imageIndex -= imageNumber;
		            stepSound = false;
		        }
		}
		else
			if(imageIndex > imageNumber) {
	            imageIndex -= imageNumber;
	            stepSound = false;
	        }
	}
		
	public void modelUpdate() {
		/*if(isPlayer())
			if(isSpinning && spinTimer % 5 == 0)
				d3d_instance_create(x+lengthdir_x(5,direction+180),y+lengthdir_y(5,direction+180),z,objSmoke);*/
		
		
		if(checkSpriteRun()) {
	        if(imageIndex > 4 && imageIndex < 6 && !stepSound) {
	        	SoundController.playSound("Footstep");
	            stepSound = true;
	            new Dust(x+Math2D.calcLenX(5,getDirection()+180),y+Math2D.calcLenY(5,getDirection()+180),z, 0, true);
	        }
	        else if(imageIndex > 0 && imageIndex < 1)
	            	stepSound = false;
	        
			
			float toUpZ;
			toUpZ = Math.abs(Math2D.calcLenY(2,imageIndex*30));
			upZ += (toUpZ - upZ)/1.5;
			/*
		        if(image_index >= 0 && image_index < 2)
		            	flyZ += -flyZ/1.5;
		        else if((image_index >= 2 && image_index < 3) || (image_index >= 5 && image_index < 6))
		            	flyZ += (1 - flyZ)/1.5; //1
		        else if(image_index >= 3 && image_index < 5)
		            	flyZ += (2 - flyZ)/1.5; //2
			*/
		}
		else {
			upZ += -upZ/1.5;
			imageIndex = 0;
		}
	}
	
	
	public void setSprite(TextureExt sprite) {
		image = sprite;
	}
	
	
	public void setSpriteRun() {
		if(getVDir() == -1)
			image = map.getRun();
		else
			image = map.getRunUp();
	}
	
	public boolean checkSpriteRun() {
		return (image == map.getRun() || image == map.getRunUp());
	}
	
	public void setSpriteStill() {
		if(getVDir() == -1)
			image = map.getStill();
		else
			image = map.getStillUp();
	}
	
	public void setSpriteJump() {
		if(getVDir() == -1)
			image = map.getJump();
		else
			image = map.getJumpUp();
	}
	
	public void setSpriteSpin() {
		image = map.getSpin();
	}
	
	public void setSpriteLand() {
		image = map.getLand();
	}
	
	
	public void updateSpin() {
		if(isSpinning) {   
		    if(spinTimer > -1)
		    {
		        spinTimer -= 1;
		        flipDir += dir*(28.8/90);
		        
		        if(isMoving && spinTimer > 20) {		        	
		        	x += Math2D.calcLenX(SPIN_VEL, getDirection());
		        	y += Math2D.calcLenY(SPIN_VEL, getDirection());
		        }
		        //direction = parCamera.direction + point_direction(0,0,heldHDirection,heldVDirection)-90;
		        /*if(heldHDirection != 0 || heldVDirection != 0)
		            vel = 2.5;
		        else
		            vel = 0;*/
		    }
		    if(spinTimer == 0) {
		        setSpriteStill();
		        flipDir = dir;
		        setDirection(GOGL.getCamDir() - dir*90);
		    }
		    if(spinTimer == -1) {
		        isSpinning = false;
		        //clearHeldDirection();
		    }
		}
	}
	
	public void land() {
	    setZVelocity(0);

	    if(inAir) {
	    	inAir = false;
	    	
			float cDir = GOGL.getCamDir();
	        new Dust(x+Math2D.calcLenX(7,cDir-90),y+Math2D.calcLenY(7,cDir-90),z+4, 0, false);
	        new Dust(x+Math2D.calcLenX(7,cDir+90),y+Math2D.calcLenY(7,cDir+90),z+4, 0, false);
	    }
	}
	
	public float getFollowX() {
		return followXP[FOLLOW_NUM-1];
	}
	public float getFollowY() {
		return followYP[FOLLOW_NUM-1];
	}
	public float getFollowFlipDir() {
		return followFDP[FOLLOW_NUM-1];
	}
	public boolean getFollowJump() {
		return followJP[FOLLOW_NUM-1];
	}
	
	protected void jump() {
		float jumpSpeed;
		switch(character)
		{
		    case "mario": jumpSpeed = JUMP_SPEED; break;
		    
		    case "luigi": jumpSpeed = 1.25f*JUMP_SPEED; break;
		    
		    case "peach":
		    case "darkpeach": jumpSpeed = .4f*JUMP_SPEED; break;
		    
		    default: jumpSpeed = JUMP_SPEED; break;
		}
		
		setZVelocity(jumpSpeed);
		z += getZVelocity();
		//buttonPrevious[0] = 1;
		isJumping = true;
		SoundController.playSound("Jump");
		    
		if(isSpinning) {
		    spinTimer = 0;
		   
		    SoundController.killSource("Spin",spinSound);
		    spinSound = null;
		}
		
		inAir = true;
		setSpriteJump();
		imageIndex = 0;
		
		
		jumpHold = 1;
		/*if(heldVDirection == 0 && heldHDirection == 0)
		    inAirDir = -1;
		else
		    inAirDir = Math2D.calcPtDir(0,0,heldHDirection,heldVDirection);*/
		if(getXYSpeed() == 0)
			inAirDir = -1;
		else
			inAirDir = getDirection();
				   
		if(!isSpinning)
			inAirSpeed = getXYSpeed();// CHAR_SPEED;
		else
			inAirSpeed = SPIN_VEL;
	}
}