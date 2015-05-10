package obj.itm;

import obj.chr.Paper;

public abstract class Item extends Paper {
	private float value;
	private String name;
	
	public Item(float x, float y, float z) {
		super(x,y,z);
		
		value = 0;
		name = "";
		type = T_ITEM;
	}
	
	public void land() {
		if(getZVelocity() < .5) {
	        setZVelocity(-getZVelocity()*.3f);
	        setXYSpeed(getXYSpeed()*.3f);
	    }
	    else
	    	setVelocity(0);
	}
	
	public void use() {
	}
	
	// ACCESSOR/MUTATOR
		public float getValue() {
			return value;
		}
		public String getName() {
			return name;
		}
}
