package gfx;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import obj.chr.Partner;
import obj.chr.Player;
import obj.env.Floor;
import obj.env.Wall;
import obj.env.blk.GroundBlock;
import obj.itm.Coin;
import obj.prim.Drawable;
import obj.prim.Instantiable;
import obj.prim.Updatable;
import snd.SoundController;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import cont.CharacterController;
import cont.GameController;
import cont.ImageController;
import cont.MessageController;
import cont.TextureController;
import dt.mat4;
import dt.vec4;
import fnt.Font;
import func.Math2D;
import func.PrintString;
import func.StringExt;

public final class GOGL {
	private static float camX, camY, camZ, focusX, focusY, focusZ, newCamX, newCamY, newCamZ, toX, toY, toZ, camDir;
	private static GLCanvas canv;
	private static GLU glu = new GLU();
	private static GLEventListener listener;
	private static GLProfile gp = GLProfile.get(GLProfile.GL2);
	private static GL2 gl;
	private static int curProgram;
	
	public static RGBA C_WHITE = new RGBA(1,1,1,1);
	public static int BORDER_LEFT = 10, BORDER_TOP = 30, SCREEN_WIDTH = 640+BORDER_LEFT*2, SCREEN_HEIGHT = 480+BORDER_TOP+BORDER_LEFT;
	
	//BACKGROUND VARIABLES
	private static BufferedImage bgImg = null;
	private static float bgX = 0;
	
	private static float R = 1, G = 1, B = 1, A = 1;
	private static PrintString prStr = new PrintString("This is pretty cool.\nCheck it out!");
	
	private static Player player;
	private static Partner partner;
	private static float orthoLayer = 0;
	
	public static void start3D(GameController gameController) {
		
		GLProfile.initSingleton();
	    //gameController.setLayout(new BorderLayout());
	
	    canv = new GLCanvas();
	    	canv.setBackground(Color.WHITE);
	    
	    	
	    listener =  new GLEventListener() {
            
	    	
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {  
            	TextureController.ini();                
            	GLText.initialize();
            	CubeMap.ini();
                
                //GroundBlocks
                
                bgImg = ImageController.loadImage("Resources/Images/Backgrounds/mountains.png", "bgMountains");
                
                
                CharacterController.ini();
            
                
                new Floor(-96,-96,96,96,0,null);
                
                
                for(int i = 0; i < 8; i++) {
                	new Wall(0,4*i,i*4,0,4*(i+1),0, TextureController.getTexture("texBricks"));
                	new Floor(0,4*i,32,4*(i+1),i*4,null);
                }
                
                /*new Wall(0,-16,32,0,16,0, TextureController.getTexture("texBricks"));
                new Wall(0,16,32,16,48,0, TextureController.getTexture("texBricks"));
                new Wall(0,-16,32,16,-48,0, TextureController.getTexture("texBricks"));*/

                
                
                player = new Player(20,20,0, "mario");
                partner = new Partner(20,20,0, "mario");

                new GroundBlock(-50,-50,0);
                
                new Coin(-20,-20,50);
                
                new Floor(96,-96,128,96,16,null);
            
                
                gl = glautodrawable.getGL().getGL2();
                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_ALPHA_TEST);
                
            	ShaderController.ini(gl);
            	//GalaxyBackground bg = new GalaxyBackground(gl);
            }
            
            public void dispose( GLAutoDrawable glautodrawable ) {
            }
            
            public void display( GLAutoDrawable glautodrawable ) { 
            	
            	Updatable.updateAll(1);
            	
            	gl = glautodrawable.getGL().getGL2();
            	gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f );
            	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
                gl.glAlphaFunc(gl.GL_GREATER, 0);
            	
            	setProjection(newCamX, newCamY, newCamZ, toX, toY, toZ);
            	draw();
            }
        };
        
        canv.addGLEventListener(listener);    
	
	    canv.setSize(gameController.getSize());
	    gameController.add(canv, BorderLayout.CENTER);
	}
	
	public static void setProjectionPrep(float cX, float cY, float cZ, float tX, float tY, float tZ) {
		newCamX = cX;
		newCamY = cY;
		newCamZ = cZ;
		toX = tX;
		toY = tY;
		toZ = tZ;
		
		camDir = Math2D.calcPtDir(camX, camY, toX, toY);
	}
	
	private static void setProjection(float cX, float cY, float cZ, float toX, float toY, float toZ) {

		float camSpeed = 5;
		float oldCZ, oldToZ, cZD, toZD;
		oldCZ = camZ;	oldToZ = toZ;
		cZD = (cZ - camZ)/(2*camSpeed);
		toZD = (toZ - focusZ)/(2*camSpeed);
		camZ += cZD;
		focusZ += toZD;
		
		//Update Listener Source
		float n, nX, nY, nZ;
		n = (float) (Math.sqrt(Math2D.sqr(toX-cX) + Math2D.sqr(toY-cY) + Math2D.sqr(focusZ-camZ)));
		nX = (toX-cX)/n;
		nY = (toY-cY)/n;
		nZ = (focusZ-camZ)/n;
		
		SoundController.updateListener(cX,cY,camZ, cX-camX,cY-camY,cZD, nX,nY,nZ, 0,0,1);
		
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) canv.getWidth() / (float) canv.getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(cX, cY, camZ, toX, toY, focusZ, 0, 0, 1);
        
        // Move Background!
        float add, d;
    	add = Math2D.calcProjDis(cX-camX, cY-camY, Math2D.calcLenX(1,camDir+90),Math2D.calcLenY(1,camDir+90));
    	d = (Math2D.calcAngDiff(Math2D.calcPtDir(camX, camY, cX, cY), camDir) > 0) ? 1 : -1;
    	bgX += .25*d*add;
    		camX = cX;	focusX = camX;
    		camY = cY;	focusY = camY;
    		//camZ = cZ;
    	
    	camDir = Math2D.calcPtDir(camX,camY,toX,toY);
        
        // Change back to model view matrix.
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();	
	}
	
	public static void repaint() {
		canv.display();
	}
	
	public static void cameraDraw() {
		
		/*if(!argument0)
		    world_draw(1);

		var camSpeed, mov;
		camSpeed = 10;//20
		if(object_index == objEditor)
		    mov = movingO;
		else
		    mov = false;
		    
		if(preset != noone)
		{
		    //From
		    xF = x+lengthdir_x(abs(lengthdir_x(zoom,zDirection)),direction+180);
		    yF = y+lengthdir_y(abs(lengthdir_x(zoom,zDirection)),direction+180);
		    zF = z+lengthdir_y(zoom,zDirection);
		   
		    //To
		    xT = x;
		    yT = y;
		    zT = z+global.cameraUp;
		}
		else if global.camFocusType != "debug"
		{
		    if(global.camFocusType == "object" && !freeze && !mov)
		    {
		        x += (global.camFocus.x-x)/camSpeed;
		        y += (global.camFocus.y-y)/camSpeed;
		        if(global.camFocus == parPlayer)
		        {
		            x = parPlayer.x;
		            y = parPlayer.y;
		            if(global.mode == "pm")
		                if(!parPlayer.spring)
		                    z += (parPlayer.floorZ-z)/(camSpeed*2);
		            else
		            {
		                z += (parPlayer.z-z)/(camSpeed*2);
		                /*if(parPlayer.zSpeed >= 0)
		                    z += (parPlayer.z-z)/(camSpeed*3);
		                else
		                    z += (mean(parPlayer.z,parPlayer.floorZ)-z)/(camSpeed*1.25);*
		            }
		        }
		        else
		            z += (global.camFocus.z-z)/camSpeed
		    }
		    else if(global.camFocusType == "point")
		    {
		        x += (global.camFocusX-x)/camSpeed;
		        y += (global.camFocusY-y)/camSpeed;
		        z += (global.camFocusZ-z)/camSpeed;
		    }
		    if freeze = 1
		        direction = point_direction(x,y,parPlayer.x,parPlayer.y)
		    
		    //location
		    x = min(global.roomXMax,max(x,global.roomXMin));
		    y = min(global.roomYMax,max(y,global.roomYMin));
		    z += global.cameraUp
		        
		    //Stick to Axis?
		    if(string(xAxis) != "")
		        x = xAxis;
		    if(string(yAxis) != "")
		        y = yAxis;
		    
		    //from
		    xF = x+lengthdir_x(abs(lengthdir_x(zoom,zDirection)),direction+180)
		    yF = y+lengthdir_y(abs(lengthdir_x(zoom,zDirection)),direction+180)
		    zF = z+lengthdir_y(zoom,zDirection) +32+global.cameraUp
		        
		    rX = xF;
		    rY = yF;
		    rZ = zF;
		    //to
		    xT = x
		    yT = y
		    zT = z+32+global.cameraUp
		}
		else
		{
		     //Stick to Axis?
		    if(string(xAxis) != "")
		        x = xAxis;
		    if(string(yAxis) != "")
		        y = yAxis;

		    //from
		    xF=x+lengthdir_x(3,direction+180)
		    yF=y+lengthdir_y(3,direction+180)
		    zF=z+lengthdir_y(3,zDirection+180)
		    //to
		    xT=x;
		    yT=y;
		    zT=z;
		}
		//draw
		freeze = 0

		var dis, dir, xDistance, yDistance;
		//dis = point_distance(xT1,yT1,xT,yT);
		//dir = point_direction(xT1,yT1,xT,yT);
		dis = point_distance(xF1,yF1,xF,yF);
		dir = point_direction(xF1,yF1,xF,yF);


		if(direction == directionPrevious)
		    panX += 2*(cos(degtorad(90+direction-dir))*dis);
		else
		    panX -= (direction - directionPrevious)/360*.75*background_get_width(tex);
		//xDistance = (xF-xF1)
		//yDistance = (yF-yF1)
		    
		xF1 = xF;
		yF1 = yF;
		xT1 = xT;
		yT1 = yT;

		d3d_set_projection_ext(xF,yF,zF,xT,yT,zT,0,0,1,pov,1.33,3,5000)





		if(object_index == objEditor)
		{
		    if(mov)
		    {
		        if(movingPrevious != mov)
		        {
		            editor_place_origin_set();
		        }
		        editor_place_arrow_draw();
		    }
		    movingPrevious = mov;
		}*/
	}

	public static void draw() {
		
		/*float[] trans = {
				camX, camY, camZ;
		}
		
		mRenderer.getModelMatCalculator().updateModelMatrix(
                pose.getTranslationAsFloats(),
                pose.getRotationAsFloats());*/
		
		//Draw BG
		setOrtho(-999);
		setColor(1,1,1,1);
			if(bgImg != null) {
				int x, y, w, h;
				
				//Position Background on Screen
				y = 0;
				h = 480;
				w = (int) (1.*h/bgImg.getHeight()*bgImg.getWidth());
				x = (int) (bgX + 4*w*camDir/360);
				
				//Draw Background Repeated
				for(int i = -2; i < 3; i++)
					if(x+i*w < w || x+i*w+w > 0)
						drawTexture(x+i*w, y, w,h, bgImg);
			}
		
			//drawGalaxyBG(gl);
			
			//tex.destroy(gl);
			
			
		setPerspective();
			
		
			//enableShaderDiffuse(gl, 2);
			//enableShader(gl, "Mirror");
		
			Drawable.drawAll();
			
			
			//enableShader(gl, "Outline");
				Shape.drawAll();
			//disableShaders(gl);
		
		drawHud();
		
		setOrtho(-999);
		setColor(1,1,1,1);
				fillRectangle(30,30,640-60,100);
				prStr.advance();
				drawString(45,45,prStr);
				
				/*vec4 plPt = calcScreenPt(player.getX(), player.getY(), (player.getZ()+20));
				vec4 prPt = calcScreenPt(partner.getX(), partner.getY(), (partner.getZ()+20));
				fillCircle(plPt.get(0), plPt.get(1), 64, 10);
				fillCircle(prPt.get(0), prPt.get(1), 64, 10);
				
		disableShaders();*/
		
	}

	public static Texture createTexture(BufferedImage img, boolean mipmap) {
		if (img == null) 
			return null;
		    
		return AWTTextureIO.newTexture(gp, img, mipmap);
	}
	
	public static void bind(Texture tex) {
		if(tex != null) {
			gl.glEnable(gl.GL_TEXTURE_2D);
			tex.enable(gl);
			tex.bind(gl);
		}
	}
	public static void unbind(Texture tex) {
		if(tex != null) {
			tex.disable(gl);
			gl.glDisable(gl.GL_TEXTURE_2D);
		}
	}

	public static float getCamDir() {
		return camDir;
	}
	
	public static void enableBlending() {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); /*GL2.GL_REPLACE);*/
	}
	public static void disableBlending() {
		gl.glDisable(GL.GL_BLEND);
	}
	
	public static vec4 calcScreenPt(float x, float y, float z) {
		vec4 pt = new vec4(camX-x, camY-y, camZ-z, 1);
		mat4 vM, pM, cM;
		vM = getViewMatrix();
		pM = getPerpProjMatrix();
		cM = pM.mult(vM);
		
		pt = cM.mult(pt);
		
		float uX, uY;
		pt.set(0,320+pt.get(0));
		pt.set(1,240+pt.get(1));
		
		return pt;
	}
	
	public static float calcPerpDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-camX, y-camY, Math2D.calcLenX(1,camDir+90),Math2D.calcLenY(1,camDir+90)));
	}
	
	public static float calcParaDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-camX, y-camY, Math2D.calcLenX(1,camDir),Math2D.calcLenY(1,camDir)));
	}
	
	
	public static void setOrtho(float useLayer) {
		orthoLayer = useLayer;
		gl.glViewport(0, 0, canv.getWidth(), canv.getHeight());
		gl.glMatrixMode (gl.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, canv.getWidth(), canv.getHeight(),0, -1000, 1000);
		gl.glMatrixMode (gl.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glDisable(gl.GL_LIGHTING);
		gl.glDisable(gl.GL_DEPTH);
		gl.glDepthFunc(gl.GL_LEQUAL);
	}
	
	public static void setPerspective() {
		gl.glMatrixMode(gl.GL_PROJECTION);
	    gl.glLoadIdentity();	 
    	setProjection(camX, camY, camZ, toX, toY, toZ);
	    gl.glMatrixMode(gl.GL_MODELVIEW);
	    gl.glLoadIdentity();
		gl.glEnable(gl.GL_DEPTH);
	}
	
	// DRAWING FUNCTIONS
	
	public static void setColor(float nR, float nG, float nB, float nA) {
		R = nR;
		G = nG;
		B = nB;
		A = nA;
		
		gl.glColor4f(R,G,B,A);
		passShaderVec4("uColor",R,G,B,A);
	}
	public static void setColor(RGBA col) {
		setColor(col.getR(),col.getG(),col.getB(),col.getA());
	}
	
	
	public static void drawTexture(float x, float y, TextureExt texExt) {
		drawTextureScaled(x,y,1,1,texExt);
	}
	public static void drawTextureScaled(float x, float y, float xS, float yS, TextureExt texExt) {
		Texture tex = texExt.getFrame(0);
		drawTextureScaled(x,y,xS,yS,tex);
	}
	
	public static void drawTexture(float x, float y, BufferedImage img) {
		drawTexture(x, y, img.getWidth(), img.getHeight(), img);
	}
	
	public static void drawTexture(float x, float y, float w, float h, BufferedImage img) {
		Texture tex = createTexture(img, false);
		
		drawTexture(x, y, w, h, tex);
		tex.destroy(gl);
	}

	public static void drawTexture(float x, float y, Texture tex) {
		drawTexture(x,y,tex.getWidth(),tex.getHeight(),tex);
	}
	public static void drawTextureScaled(float x, float y, float xS, float yS, Texture tex) {
		drawTexture(x,y,tex.getWidth()*xS,tex.getHeight()*yS,tex);
	}
	public static void drawTexture(float x, float y, float w, float h, Texture tex) {

		if(tex != null) {
			gl.glEnable(gl.GL_TEXTURE_2D);
			tex.bind(gl);
			tex.enable(gl);
			
			gl.glEnable(GL.GL_BLEND); 
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
			//gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);	 // ALLOWS FOR COLOR-SETTING!!!		
		}
		
		fillRectangle(x,y,w,h);
      	
        if(tex != null) {
	      	gl.glDisable(gl.GL_TEXTURE_2D);
	      	tex.disable(gl);
        }
	}
	
	public static void fillRectangle(float x, float y, float w, float h) {				
		x += BORDER_LEFT;
		y += BORDER_TOP;

		gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(1.0, 1.0);
				gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(0.0, 1.0);
				gl.glVertex3f(x,y+h, orthoLayer);
        gl.glEnd();      	      	
	}
	
	public static void fillCircle(vec4 vec, float r, int numPts) {
		fillCircle(vec.get(0), vec.get(1), r, numPts);
	}

	public static void fillCircle(float x, float y, float r, int numPts) {				
		x += BORDER_LEFT;
		y += BORDER_TOP;

		gl.glBegin(gl.GL_TRIANGLE_FAN);
			gl.glTexCoord2d(0.5, .5);
				gl.glVertex3f(x, y, orthoLayer);
				
			float dir, xN, yN;
			
			for(int i = 0; i < numPts; i++) {
				dir = 360.f*i/(numPts-1);
				
				xN = Math2D.calcLenX(dir);
				yN = Math2D.calcLenY(dir);
				
				gl.glTexCoord2d(.5+.5*xN, .5+.5*yN);
					gl.glVertex3f(x+xN*r, y+yN*r, orthoLayer);
			}

        gl.glEnd();
	}


	public static float drawChar(float x, float y, char c) {
		return GLText.drawChar(x,y,1,1,c);
	}
	public static float drawChar(float x, float y, float xS, float yS, char c) {
		return GLText.drawChar(x,y, xS,yS, c);
	}
	
	
	// Text Functions
	
	public static void drawString(float x, float y, String str) {
		GLText.drawString(x,y,1,1,str);
	}
	public static void drawString(float x, float y, float xS, float yS, String str) {
		GLText.drawString(x,y, xS,yS, str);
	}
	public static void drawString(float x, float y, PrintString str) {
		GLText.drawString(x,y,1,1,str);
	}
	public static void drawString(float x, float y, float xS, float yS, PrintString str) {
		GLText.drawString(x,y, xS,yS, str);
	}
	public static void drawStringCentered(float x, float y, String str) {
		GLText.drawStringCentered(x,y,1,1,str);
	}
	public static void drawStringCentered(float x, float y, float xS, float yS, String str) {
		GLText.drawStringCentered(x,y,xS,yS,str);
	}

	public static float getStringWidth(String str) {
		return GLText.getStringWidth(1,1,str);
	}
	public static float getStringWidth(float xS, float yS, String str) {
		return GLText.getStringWidth(xS,yS,str);
	}
	
	public static float getStringHeight(String str) {
		return GLText.getStringHeight(1,1,str);
	}
	public static float getStringHeight(float xS, float yS, String str) {
		return GLText.getStringHeight(xS,yS,str);
	}

	
	// 3D Functions
	
		// Transformation Functions
			public static void transformClear() {
				gl.glLoadIdentity();
			}
			public static void transformTranslation(float x, float y, float z) {
				gl.glTranslatef(x,y,z);
			}
			public static void transformScale(float s) {
				transformScale(s,s,s);
			}
			public static void transformScale(float xS, float yS, float zS) {
				gl.glScalef(xS, yS, zS);
			}
			public static void transformRotationX(float ang) {
				gl.glRotatef(ang, 1,0,0);
			}
			public static void transformRotationY(float ang) {
				gl.glRotatef(ang, 0,1,0);
			}
			public static void transformRotationZ(float ang) {
				gl.glRotatef(ang, 0,0,1);
			}
			
		// Wall-Drawing Function
			
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2) {
				draw3DWall(x1,y1,z1,x2,y2,z2,null);
			}
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
				
				if(tex != null) {
					gl.glEnable(gl.GL_TEXTURE_2D);
					tex.bind(gl);
					tex.enable(gl);
				}
				
				gl.glBegin(gl.GL_QUADS);
				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(x1, y1, z1);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(x2, y2, z1);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(x2, y2, z2);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(x1, y1, z2);
				gl.glEnd();
				
				unbind(tex);
			}
			
			public static void draw3DFloor(float x1, float y1, float x2, float y2, float z) {
				draw3DFloor(x1,y1,x2,y2,z,null);
			}
			public static void draw3DFloor(float x1, float y1, float x2, float y2, float z, Texture tex) {
				
				bind(tex);
				
				gl.glBegin(gl.GL_QUADS);
				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(x1, y1, z);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(x2, y1, z);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(x2, y2, z);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(x1, y2, z);
				gl.glEnd();
				
				unbind(tex);
			}
			
		// Tetrahedron-Drawing Function
			public static void draw3DTetrahedron(float r, float h) {
				draw3DFrustem(r,h,null);
			}
			public static void draw3DTetrahedron(float r, float h, Texture tex) {
				draw3DTetrahedron(0,0,0,r,h,tex);
			}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h) {
				draw3DTetrahedron(x,y,z,r,h,null);
			}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h, Texture tex) {
				draw3DFrustem(x,y,z,r,0,h,tex,4);
			}
	
		// Frustem-Drawing Functions
			public static void draw3DFrustem(float r, float h) {
				draw3DFrustem(r,h,null);
			}
			public static void draw3DFrustem(float r, float h, int numPts) {
				draw3DFrustem(r,r,h,numPts);
			}
			public static void draw3DFrustem(float r, float h, Texture tex) {
				draw3DFrustem(r,r,h,tex);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h) {
				draw3DFrustem(radBot,radTop, h, null);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex) {
				draw3DFrustem(0,0,0,radBot,radTop, h, tex);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, int numPts) {
				draw3DFrustem(0,0,0,radBot,radTop, h, null, numPts);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex, int numPts) {
				draw3DFrustem(0,0,0,radBot,radTop, h, tex, numPts);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h) {
				draw3DFrustem(0,0,0,radBot,radTop, h, null);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex) {
				draw3DFrustem(0,0,0,radBot,radTop, h, tex, 10);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, int numPts) {
				draw3DFrustem(0,0,0,radBot,radTop, h, null);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex, int numPts) {
				
				if(numPts < 3)
					return;
				
				bind(tex);
				
				float dir, zDir, xN, yN, zN;
				
				// Draw Top	
				if(radTop > 0) {
					gl.glBegin(gl.GL_TRIANGLE_FAN);
					gl.glTexCoord2d(.5,.5);
						gl.glVertex3f(x,y,z+h);
					for(int i = 0; i < numPts; i++) {
						dir = 360.f*i/(numPts-1);
												
						xN = Math2D.calcLenX(1,dir);
						yN = Math2D.calcLenY(1,dir);	
						
						gl.glTexCoord2d(.5+.5*xN,.5+.5*yN);
							gl.glVertex3f(x+xN*radTop,y+yN*radTop,z+h);
					}
			        gl.glEnd();
				}
				
				if(radBot > 0) {
					gl.glBegin(gl.GL_TRIANGLE_FAN);	
					gl.glTexCoord2d(.5,.5);
						gl.glVertex3f(x,y,z);
					for(int i = 0; i < numPts; i++) {
						dir = 360.f*i/(numPts-1);
												
						xN = Math2D.calcLenX(1,dir);
						yN = Math2D.calcLenY(1,dir);	
						
						gl.glTexCoord2d(.5+.5*xN,.5+.5*yN);
							gl.glVertex3f(x+xN*radBot,y+yN*radBot,z);
					}
			        gl.glEnd();
				}
				
				if(h > 0) {
					gl.glBegin(gl.GL_TRIANGLE_STRIP);			
					for(int i = 0; i < numPts; i++) {
						dir = 360.f*i/(numPts-1);
												
						xN = Math2D.calcLenX(1,dir);
						yN = Math2D.calcLenY(1,dir);	
						
						gl.glTexCoord2d(i/(numPts-1),0);
							gl.glVertex3f(x+xN*radTop,y+yN*radTop,z+h);
						gl.glTexCoord2d(i/(numPts-1),1);
							gl.glVertex3f(x+xN*radBot,y+yN*radBot,z);
					}
			        gl.glEnd();
				}
				
				unbind(tex);
	
			}
	
		// Sphere-Drawing Functions
			public static void draw3DSphere(float r) {
				draw3DSphere(0,0,0,r,null);
			}	
			public static void draw3DSphere(float r, int numPts) {
				draw3DSphere(0,0,0,r,null,numPts);
			}	
			public static void draw3DSphere(float r, Texture tex) {
				draw3DSphere(0,0,0,r,tex);
			}	
			public static void draw3DSphere(float r, Texture tex, int numPts) {
				draw3DSphere(0,0,0,r,tex,numPts);
			}	
			public static void draw3DSphere(float x, float y, float z, float r) {
				draw3DSphere(x,y,z,r,null);
			}	
			public static void draw3DSphere(float x, float y, float z, float r, int numPts) {
				draw3DSphere(x,y,z,r,null,numPts);
			}
			public static void draw3DSphere(float x, float y, float z, float r, Texture tex) {
				draw3DSphere(x,y,z,r,tex,10);
			}
			public static void draw3DSphere(float x, float y, float z, float r, Texture tex, int numPts) {		
			
				if(numPts < 3)
					return;
				
				bind(tex);
				
				float dir, zDir, xN, yN, zN;
				
				for(int d = 0; d < numPts-1; d++) {
					
					gl.glBegin(gl.GL_TRIANGLE_STRIP);
					
					for(int i = 0; i < numPts; i++) {
						dir = 360.f*i/(numPts-1);
						
						for(int ii = 0; ii < 2; ii++) {
							zDir = 360.f*(d+ii)/(numPts-1);
							
							xN = Math2D.calcPolarX(1,dir,zDir);
							yN = Math2D.calcPolarY(1,dir,zDir);
							zN = Math2D.calcPolarZ(1,dir,zDir);
							
							gl.glTexCoord2d(i/(numPts-1), (d+ii)/(numPts-1));
								gl.glVertex3f(x+xN*r,y+yN*r,z+zN*r);
						}
					}
					
			        gl.glEnd();
				}
				
				unbind(tex);
			}

	public static void drawHud() {
		int instNum, upNum, drawNum, shapeNum;
		instNum = Instantiable.getNumber();
		upNum = Updatable.getNumber();
		drawNum = Drawable.getNumber();
		shapeNum = Shape.getNumber();
		
		setOrtho(-3);
		
		/*System.out.println("");
		System.out.println(instNum);
		System.out.println(upNum);
		System.out.println(drawNum);
		System.out.println(shapeNum);*/
		
		//MessageController.draw(gl);
		
		setPerspective();
	}
	


	// this function is called when you want to activate the shader.
    // Once activated, it will be applied to anything that you draw from here on
    // until you call the dontUseShader(GL) function.
    public static void enableShader(String shaderName) {
    	
    	Shader shader = ShaderController.getShader(shaderName);
    	curProgram = shader.getProgram();
    	
	    float[] resI = {
	    		SCREEN_WIDTH, SCREEN_HEIGHT
	    };
	    
	    gl.glUseProgram(curProgram);
		gl.glUniform2fv(gl.glGetUniformLocation(curProgram, "iResolution"), 1, resI, 0);			
    	gl.glUniform1f(gl.glGetUniformLocation(curProgram, "iGlobalTime"), getTime()/50f);        
    	passShaderVec4("uColor",R,G,B,A);
    }
    
    public static float getTime() {
    	return TextureController.getTime();
    }
    
    public static boolean passShaderVec4(String name, float a, float b, float c, float d) {

    	if(curProgram == 0)
    		return false;
    	
    	float[] pass = {
    		a, b, c, d
    	};
       	
		gl.glUniform4fv(gl.glGetUniformLocation(curProgram, name), 1, pass, 0);
		return true;
    }

    public static void enableShaderGrayscale() {
    	gl.glUseProgram(ShaderController.getShader("Grayscale").getProgram());
    }

    public static void enableShaderDiffuse(float radius) {

    	enableShader("Diffuse");
    	gl.glUniform1f(gl.glGetUniformLocation(curProgram, "iRadius"), radius);
    }	
	    

    // when you have finished drawing everything that you want using the shaders, 
    // call this to stop further shader interactions.
    public static void disableShaders() {
        gl.glUseProgram(0);
        curProgram = 0;
    }
    
    
    public static void drawGaussian() {
    	/*
    	 * enableShader(gl, ShaderController.getShader("Gaussian"));
	    	
	    	int shaderprogram = ShaderController.getShader("Gaussian").getProgram();
	
		    float[] resI = {
		    		640, 480
		    };
			
			int res = gl.glGetUniformLocation(shaderprogram, "iResolution");
			int globTime = gl.glGetUniformLocation(shaderprogram, "iGlobalTime");
			gl.glUniform1f(gl.glGetUniformLocation(shaderprogram, "iRadius"), 20);
			gl.glUniform2fv(res, 1, resI, 0);			
    	 */
    }
    
    public static void drawGalaxyBG() {
    	enableShader("Galaxy");
    	
		fillRectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		disableShaders();
    }

    public static mat4 getPerpProjMatrix() {
    	return getPerpProjMatrix(0, canv.getWidth(), canv.getHeight(),0, -1000, 1000);
    }
    
    public static mat4 getPerpProjMatrix(float l, float r, float b, float t, float n, float f) {
    	return (new mat4(2*n/(r-l),0,(r+l)/(r-l),0,
    					0,2*n/(t-b),(t+b)/(t-b),0,
    					0,0,-(f+n)/(f-n),-2*f*n/(f-n),
    					0,0,-1,0)).transpose();
    }
    
    public static mat4 getOrthoProjMatrix() {
    	return getOrthoProjMatrix(0, canv.getWidth(), canv.getHeight(),0, -1000, 1000);
    }
    public static mat4 getOrthoProjMatrix(float l, float r, float b, float t, float n, float f) {
    	return (new mat4(2/(r-l),0,0,-(r+l)/(r-l),
    					0,2/(t-b),0,-(t+b)/(t-b),
    					0,0,-2/(f-n),-(f+n)/(f-n),
    					0,0,0,1)).transpose();
    }
    
    public static mat4 getViewMatrix() {

 		float eyeX, eyeY, eyeZ, upX, upY, upZ, fX, fY, fZ;
 		eyeX = camX;
 		eyeY = camY;
 		eyeZ = camZ;
    	vec4 camNorm = new vec4(toX-camX, toY-camY, toZ-camZ, 0);
    	
    	camNorm.println();
    	
 		// Get Camera Position, Looking Normal, and Up Normal
 		
 		camNorm.set(camNorm.norm());
 		fX = camNorm.get(0);
 		fY = camNorm.get(1);
 		fZ = camNorm.get(2);
 		upX = 0;
 		upY = 0;
 		upZ = 1;
 		
 		// Forward Direction
 		float lX, lY, lZ;
 		lX = fX;
 		lY = fY;
 		lZ = fZ;

 		// S = L x U
 		float sX, sY, sZ, sN;
 		sX = lY*upZ - lZ*upY;
 		sY = lZ*upX - lX*upZ;
 		sZ = lX*upY - lY*upX;
 		sN = (float) Math.sqrt(sX*sX + sY*sY + sZ*sZ);
 		sX /= sN;
 		sY /= sN;
 		sZ /= sN;

 		// U' = S x L
 		float uX, uY, uZ;
 		uX = sY*lZ - sZ*lY;
 		uY = sZ*lX - sX*lZ;
 		uZ = sX*lY - sY*lX;

 		// Create View Matrix
 		mat4 viewMat = new mat4(sX,uX,-lX,-eyeX,sY,uY,-lY,-eyeY,sZ,uZ,-lZ,-eyeZ,0,0,0,1);
 		//mat4 viewMat = new mat4(sX,uX,-lX,0,sY,uY,-lY,0,sZ,uZ,-lZ,0,0,0,0,1);
 		// Transpose View Matrix
 		return viewMat.transpose();
    }
}