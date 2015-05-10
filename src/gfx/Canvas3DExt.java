package gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;
import javax.swing.JPanel;

import cont.GameController;
import cont.TransitionController;
import func.Math2D;

public class Canvas3DExt extends Canvas3D {
	private static BufferedImage bgImg = null;
	private J3DGraphics2D g2d;
	private int w, h;
	private static List<BufferedImage> blurImgs = new ArrayList<BufferedImage>();
	private static JPanel myPanel = null;
	private boolean stop = false, blur = false;
	private int blurNum = 3;
    private static double bgX = 0;

	
	public Canvas3DExt(GraphicsConfiguration config, JPanel panel) {
		super(config);
		
		myPanel = panel;
				
		for(int i = 0; i < blurNum; i++)
    		blurImgs.add(new BufferedImage(640,480, BufferedImage.TYPE_INT_ARGB));
		
		g2d = getGraphics2D();
				
		w = GOGL.SCREEN_WIDTH;
		h = GOGL.SCREEN_HEIGHT;
	}
	
	public void preRender() {		
		if(bgImg != null) {
			int x, y, w, h;
			
			//Position Background on Screen
			y = 0;
			h = 480;
			w = (int) (1.*h/bgImg.getHeight()*bgImg.getWidth());
			x = (int) (getBgX() + 4*w*Graphics3D.getCamDir()/360);
			
			//Draw Background Repeated
			for(int i = -2; i < 3; i++)
				if(x+i*w < w || x+i*w+w > 0)
					g2d.drawImage(bgImg, x+i*w, y, w, h, null);
		}
		
		g2d.flush(false);		
	}
	
	public void postRender() {
		if(!blur || stop)
			return;
		
		BufferedImage newBase = addAlpha(getScreenShot3D());
		
		for(int i = 1; i < blurNum; i++) {
			blurImgs.set(i, blurImgs.get(i-1));
    		
    		float[] scales = { 1f, 1f, 1f, (float) ((blurNum-i)/(blurNum-1)*.6)};
    		float[] offsets = new float[4];
    		RescaleOp rop = new RescaleOp(scales, offsets, null);
    		g2d.drawImage(blurImgs.get(i), rop, 0, 0);
 		}
		
		blurImgs.set(0, newBase);
		
		
		//OverlayController.draw(g2d);
		TransitionController.draw(g2d);
		
		g2d.flush(false);
	}
	
	public void setBackground(BufferedImage bg) {
		this.bgImg = bg;
	}
	
	protected BufferedImage getScreenShot3D() {
		Canvas3D canvas = this;
		Point p = new Point();
		p = canvas.getLocationOnScreen();
		Rectangle bounds = new Rectangle(p.x, p.y, canvas.getWidth(), canvas.getHeight());
		
		
		try{
			Robot robot = new Robot(myPanel.getGraphicsConfiguration().getDevice());
			return robot.createScreenCapture(bounds);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	static BufferedImage addAlpha(BufferedImage bi) {
	     int w = bi.getWidth();
	     int h = bi.getHeight();
	     int type = BufferedImage.TYPE_INT_ARGB;
	     BufferedImage bia = new BufferedImage(w,h,type);
	     Graphics2D g = bia.createGraphics();
	     g.drawImage(bi, 0, 0, null);
	     g.dispose();
	     return bia;
	}

	public void stop() {
		stop = true;
	}

	public static double getBgX() {
		return bgX;
	}

	public static void setBgX(double bgX) {
		//Canvas3DExt.bgX = Math2D.wrap(0, bgX, (1.*480/bgImg.getHeight()*bgImg.getWidth()));
	}
}
