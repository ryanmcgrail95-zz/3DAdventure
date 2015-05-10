package obj.prim;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import cont.GameController;
import cont.TextureController;
import dt.SortedList;
import snd.SoundController;

public abstract class Updatable {
	private static SortedList<Updatable> updateList = new SortedList<Updatable>();


	public Updatable() {
		updateList.add(this);
	}
	
	public abstract void update(float deltaT);
	
	public void destroy() {
		updateList.remove(this);
	}
	
	public static void clean() {
		updateList.clean();
	}
		
	//Global Functions
		public static void updateAll(float deltaT) {
			GameController.input.update();
			TextureController.update(deltaT);
		    						
			Updatable u;
			for(int i = 0; i < updateList.size(); i++) {
				u = updateList.get(i);
				u.update(deltaT);
			}
			
			Drawable.sort();
			
			Drawable.clean();
			Updatable.clean();
			Physical.clean();
			Environmental.clean();
			SoundController.clean();
		}

		public static int getNumber() {
			return updateList.size();
		}		
}
