package cont;

import gfx.TextureExt;

import java.util.HashMap;
import java.util.Map;

public class ItemController {
	private static Map<String, String> itemInfoMap = new HashMap<String, String>();
	private static Map<String, TextureExt> itemSpriteMap = new HashMap<String, TextureExt>();
	
	//ITEMS
		
		private static void addItem(String name, TextureExt sprite, String info) {
			itemSpriteMap.put(name, sprite);
			itemInfoMap.put(name,  info);
		}
	
		public static TextureExt getItemSprite(String name) {
			return itemSpriteMap.get(name);
		}
		
		public static String getItemInfo(String name) {
			return itemInfoMap.get(name);
		}
}
