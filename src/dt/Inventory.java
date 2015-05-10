package dt;

import obj.itm.Item;

public class Inventory {
	private SortedList<Item> inv;
	private static final byte S_VALUE = 0, S_NAME = 1;
	private byte sortType = S_VALUE;
	
	public Inventory() {
		inv = new SortedList<Item>();
	}
	
	public void draw() {
		
	}
	
	
	public int size() {
		return inv.size();
	}
	
	public void addItem(Item obj) {
		inv.add(obj);
		sort();
	}
	
	public void sort() {
		switch(sortType) {
			case S_VALUE:
				sortValue(true);
				break;
				
			case S_NAME:
				sortName(true);
				break;
		}
	}
	
	public void sortName(boolean forward) {
		String str;
		char curC;
		int si = inv.size();
		
		for(int i = 0; i < si; i++)
			inv.setString(i, inv.get(i).getName());
		
		inv.sort(SortedList.M_STRING, forward);
	}
	
	public void sortValue(boolean forward) {
		int si = inv.size();
		
		for(int i = 0; i < si; i++)
			inv.setValue(i, inv.get(i).getValue());
		
		inv.sort(SortedList.M_NUMBER, forward);
	}
}
