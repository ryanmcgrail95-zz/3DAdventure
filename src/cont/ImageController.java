package cont;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;

import com.sun.j3d.utils.image.TextureLoader;

import fl.FileExt;

public class ImageController {
	private static Map<String, BufferedImage> imgMap = new HashMap<String, BufferedImage>();
		
	public static BufferedImage loadImage(String fileName, String name) {			
		BufferedImage img = loadImage(fileName);
		imgMap.put(name, img);
        
        return img;
	}
	
	public static BufferedImage loadImage(String fileName) {
		try {
			return ImageIO.read(FileExt.get(fileName));
			//return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage getImage(String name) {
		return imgMap.get(name);
	}
}
