package cont;

import gfx.GOGL;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

public class MessageController {
	private static int w = 256;
	private static List<Message> messageList = new ArrayList<Message>();
	private static BufferedImage messageImg = new BufferedImage(w,w, BufferedImage.TYPE_INT_ARGB);
	private static Graphics2D messageGfx = (Graphics2D) messageImg.getGraphics();
	
	private static class Message {
		private String text;
		private float steps;
		
		
		public Message(String text, float steps) {
			this.text = text;
			this.steps = steps;
			
			messageList.add(this);
		}
		
		public void incrementTime(float time) {
			steps -= time;
		}
		
		public boolean isDone() {
			return (steps <= -1);
		}

		public String getText() {
			return text;
		}
		public float getTime() {
			return steps;
		}
	}
	
	
	public static void addMessage(String text, float steps) {
		//Adds a message that will appear in the upper left-hand corner. Useful for debugging or a Skyrim-esque status system. The message will stay
		//visible for the number of steps given; I recommend using time_to_steps() in conjunction with this.
		
		new Message(text, steps);
	}
	
	public static void draw(GL2 gl) {
		messageGfx.setBackground(new Color(0,0,0,0));
		messageGfx.clearRect(0,0, w,w);
		
		List<Message> destroyList = new ArrayList<Message>();
		Message curMessage;
		int dY = 0;

		for(int i = 0; i < messageList.size(); i += 1) {
			curMessage = messageList.get(i);
			
		    //Iterate Timers for Messages
			curMessage.incrementTime(1);
		    
		    //Sort Through and Eliminate Messages Whose Timers are -1
		    if(curMessage.isDone()) {
		        destroyList.add(curMessage);   
		    }
		    else {
		    	//draw_set_font(fntRetro);

		    	    
		    	if(curMessage.getTime() < 20)
		    		new Color(0f,0f,0f,curMessage.getTime()/20);
		    	else
		    	    messageGfx.setColor(new Color(0f,0f,0f,1f));
		    	    
	    	    //Draw Message
	    	    messageGfx.drawString(curMessage.getText(), 0, dY);

	    	    //Move Next Message Down
	    	    dY += 20;
		    }
		}
		
		
		//Draw Message to Screen
		Texture tex = GOGL.createTexture(messageImg, false);
		
		tex.enable(gl);
		tex.bind(gl);

		gl.glEnable(GL.GL_BLEND); 
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

				
		int h = 480-100;
		
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2d(0.0, 0.0); 	
		gl.glVertex2d(0, h);
		gl.glTexCoord2d(1.0, 0.0);
		gl.glVertex2d(w, h);
		gl.glTexCoord2d(1.0, 1.0);
		gl.glVertex2d(w, h-w);
		gl.glTexCoord2d(0.0, 1.0);
		gl.glVertex2d(0, h-w);
		gl.glEnd();			
		
		tex.disable(gl);
		gl.glDisable(GL.GL_BLEND); 

		
		
		//Remove Old Messages
		for(Message m : destroyList)
			messageList.remove(m);
	}
}
