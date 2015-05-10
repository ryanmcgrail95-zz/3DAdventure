package obj.prim;
import gfx.Shape;

import java.awt.Graphics2D;
import javax.media.j3d.Shape3D;
import dt.SortedList;

public class Drawable extends Instantiable {
	private static SortedList<Drawable> drawList = new SortedList<Drawable>();


	protected Shape shape;
	protected double rotX = 0, rotY = 0, rotZ = 0;
	protected boolean visible = true;
	
	public Drawable() {
		super();
		
		drawList.add(this);
	}
	
	//PARENT FUNCTIONS
		public void update(float deltaT) {		
			super.update(deltaT);
		}
		
		public boolean draw() {
			return true;
		}
	
		public void destroy() {
			drawList.remove(this);
			
			super.destroy();
			if(shape != null)
				shape.destroy();
		}
		
		
	//PERSONAL FUNCTIONS
		public void setVisible(boolean visible) {
			this.visible = visible;
		}
	
		
		
	//GLOBAL FUNCTIONS
		public static void drawAll() {			
			int si = drawList.size();
			Drawable d;
			
			for(int i = 0; i < si; i++) {
				d = drawList.get(i);
				
				if(d.visible)
					d.draw();
			}
		}
		
		public static void clean() {
			
		}
		
		public static void sort() {
			int si = drawList.size();
			float dist;
			
			for(int i = 0; i < si; i++) {
				dist = drawList.get(i).calcCamDis();
				drawList.setValue(i, dist);
			}
			
			drawList.sortReverse();
		}

		private float calcCamDis() {
			return 0;
		}

		public static int getNumber() {
			return drawList.size();
		}
}
