package gfx;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Geometry;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import obj.env.Wall;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cont.TextureController;
import func.Math2D;

public class Graphics3D {
	private static SimpleUniverse univ = null;
    private static Canvas3DExt canvas = null;
    
    private static BranchGroup sceneGroup = null;
    	private static OrderedGroup scene = null;
    private static Shape bgObj = null;
    private static float scale = 1.f/16;
    
    //CAMERA VARIABLES
    private static float camX = 0, camY = 0, camZ = 0, camDir = 0;
    
    public static void start3D(JPanel panel) {
       	// Get the preferred graphics configuration for the default screen
    	GraphicsConfiguration config =
    	    SimpleUniverse.getPreferredConfiguration();

    	// Create a Canvas3D using the preferred configuration
    	canvas = new Canvas3DExt(config, panel);
    	
    	    	
    	// Create simple universe with view branch
    	univ = new SimpleUniverse(canvas);
    	sceneGroup = new BranchGroup();
    	scene = new OrderedGroup();
    		scene.setCapability(OrderedGroup.ALLOW_CHILDREN_EXTEND);
    		scene.setCapability(OrderedGroup.ALLOW_CHILDREN_READ);
    		scene.setCapability(OrderedGroup.ALLOW_CHILDREN_WRITE);
    	
    		sceneGroup.addChild(scene);
    		univ.addBranchGraph(sceneGroup);
    	
    	
    	
    	// Ensure at least 5 msec per frame (i.e., < 200Hz)
    	View v = univ.getViewer().getView();
    	v.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
    	v.stopView();
    	v.setBackClipDistance(1000);
    	v.setFieldOfView(35./180*Math.PI);
    	v.setDepthBufferFreezeTransparent(false);

	
    	panel.add(canvas, java.awt.BorderLayout.CENTER);
    }
    
    public static void draw() {
    	univ.getViewer().getView().renderOnce();
    }
    
    
    public static void setProjection(float cX, float cY, float cZ, float toX, float toY, float toZ) { 	    	
    	Transform3D pos = new Transform3D();
    	//pos.setTranslation(new Vector3d(cX, cY, cZ));
    	pos.lookAt(new Point3d(cX,cY,cZ), new Point3d(toX,toY,toZ), new Vector3d(0,0,1));
    	pos.invert();
    	
    	//System.out.println(camX);	
    	
    	float add, d;
    	add = Math2D.calcProjDis(cX-camX, cY-camY, Math2D.calcLenX(1,camDir+90),Math2D.calcLenY(1,camDir+90));
    	d = (Math2D.calcAngDiff(Math2D.calcPtDis(camX, camY, cX, cY), camDir) > 0) ? 1 : -1;
    	canvas.setBgX(canvas.getBgX() + .25f*d*add);
    		camX = cX;
    		camY = cY;
    		camZ = cZ;
    	
    	camDir = Math2D.calcPtDir(camX,camY,toX,toY);
    	
    	univ.getViewingPlatform().getViewPlatformTransform().setTransform(pos);    	
    }
    
    public static Shape3D createWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture2D tex) {
    	/*x1 *= scale;
    	y1 *= scale;
    	z1 *= scale;
    	x2 *= scale;
    	y2 *= scale;
    	z2 *= scale;*/
    	
    	// Default Material node for Picture
        Material pictureMaterial = (new Material(
                new Color3f(1f, 0f, 0f),
                new Color3f(1f, 0f, 0f),
                
                new Color3f(1f, 0f, 0f),
                new Color3f(1f, 0f, 0f),
                1f));
    	
    	Shape3D wall;
    	Appearance app = new Appearance();
    	app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
    	
    	Point3d coords[] = new Point3d[4];
        Point2f textCoords[] = new Point2f[4];

        coords[0] = new Point3d(x1, y1, z1);
        coords[1] = new Point3d(x1, y1, z2);
        coords[2] = new Point3d(x2, y2, z2);
        coords[3] = new Point3d(x2, y2, z1);

        //  Create an array of texture coordinates (default)
        textCoords[0] = new Point2f(0.0f, 1.0f);
        textCoords[1] = new Point2f(0.0f, 0.0f);
        textCoords[2] = new Point2f(1.0f, 0.0f);
        textCoords[3] = new Point2f(1.0f, 1.0f);
    	
        
        app.setTexture(tex);
        
        
        	
        TransparencyAttributes trAtt = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.0f);	
        	trAtt.setTransparencyMode(TransparencyAttributes.NICEST);
        	app.setTransparencyAttributes(trAtt);
        	
        
        TextureAttributes att = new TextureAttributes();
	        // Use 'NICEST' for highest quality rendering
	        att.setPerspectiveCorrectionMode(TextureAttributes.NICEST);
	        att.setCombineAlphaMode(TextureAttributes.COMBINE_SRC_ALPHA);
	        att.setTextureMode(TextureAttributes.MODULATE);
	        
        // Setup a PolygonAttributes
        PolygonAttributes mat = new PolygonAttributes();
	        // Render all sides of the shape
	        mat.setCullFace(PolygonAttributes.CULL_NONE);
	        // Create normals for the back of shape also, so light can reflect there too, etc
	        mat.setBackFaceNormalFlip(true);

        // Add Material, TextureAttributes and PolygonAttributes to the Appearance
        app.setTextureAttributes(att);
        app.setMaterial(pictureMaterial);
        app.setPolygonAttributes(mat);
        
        
        // Create a QUAD_ARRAY GeometryInfo
        GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

        // Tell it the coordinates of the QUADs
        gi.setCoordinates(coords);

        // Tell it the texture coordinates of the QUADs
        gi.setTextureCoordinates(textCoords);
        
        
        wall = new Shape3D(gi.getGeometryArray(), app);
    	    	
    	return wall;
    }
    
    public static Shape3D createFloor(float x1, float y1, float x2, float y2, float z, Texture2D tex) {
    	// Default Material node for Picture
        Material pictureMaterial = (new Material(
                new Color3f(1f, 0f, 0f),
                new Color3f(1f, 0f, 0f),
                
                new Color3f(1f, 0f, 0f),
                new Color3f(1f, 0f, 0f),
                1f));
    	
    	Shape3D floor;
    	Appearance app = new Appearance();
    	app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
    	
    	Point3d coords[] = new Point3d[4];
        Point2f textCoords[] = new Point2f[4];

        coords[0] = new Point3d(x1, y1, z);
        coords[1] = new Point3d(x2, y1, z);
        coords[2] = new Point3d(x2, y2, z);
        coords[3] = new Point3d(x1, y2, z);

        //  Create an array of texture coordinates (default)
        textCoords[0] = new Point2f(0.0f, 1.0f);
        textCoords[1] = new Point2f(0.0f, 0.0f);
        textCoords[2] = new Point2f(1.0f, 0.0f);
        textCoords[3] = new Point2f(1.0f, 1.0f);
    	
        
        app.setTexture(tex);

        
                
        TransparencyAttributes trAtt = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.0f);
        	trAtt.setTransparencyMode(TransparencyAttributes.NICEST);        	
        	app.setTransparencyAttributes(trAtt);
        	
        
        TextureAttributes att = new TextureAttributes();
	        // Use 'NICEST' for highest quality rendering
	        att.setPerspectiveCorrectionMode(TextureAttributes.NICEST);
	        //System.out.println(att.getCombineAlphaMode() == TextureAttributes.COMBINE_SRC_ALPHA);
	        att.setCombineAlphaMode(TextureAttributes.COMBINE_SRC_ALPHA);
	        att.setTextureMode(TextureAttributes.MODULATE);
	        
        // Setup a PolygonAttributes
        PolygonAttributes mat = new PolygonAttributes();
	        // Render all sides of the shape
	        mat.setCullFace(PolygonAttributes.CULL_NONE);
	        // Create normals for the back of shape also, so light can reflect there too, etc
	        mat.setBackFaceNormalFlip(true);
	        
	        
        // Add Material, TextureAttributes and PolygonAttributes to the Appearance
        app.setTextureAttributes(att);
        if(tex != null)
        	app.setMaterial(pictureMaterial);
        app.setPolygonAttributes(mat);
        
        
        // Create a QUAD_ARRAY GeometryInfo
        GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

        // Tell it the coordinates of the QUADs
        gi.setCoordinates(coords);

        // Tell it the texture coordinates of the QUADs
        gi.setTextureCoordinates(textCoords);
        
        
        floor = new Shape3D(gi.getGeometryArray(), app);
    	    	
    	return floor;
    }
    
    
    
    /*public static void addShape(Shape shape) {
    	//univ.addBranchGraph(shape);
    	scene.addChild(shape);
    }*/
    
    public static void setBackgroundImage(BufferedImage img) {
    	canvas.setBackground(img);
    }
    
    public static float getCamDir() {
    	return camDir;
    }


    public static void stop() {
    	canvas.stop();
    }


	/*public static Shape createBlock(float x1, float y1, float z1, float x2, float y2, float z2, Texture2D tex) {
		List<Shape3D> shapeList = new ArrayList<Shape3D>();
			shapeList.add(Graphics3D.createWall(x1, y1, z1, x2, y1, z2, tex));
			shapeList.add(Graphics3D.createWall(x2, y1, z1, x2, y2, z2, tex));
			shapeList.add(Graphics3D.createWall(x1, y1, z1, x1, y2, z2, tex));
			shapeList.add(Graphics3D.createWall(x1, y2, z1, x2, y2, z2, tex));
			shapeList.add(Graphics3D.createFloor(x1, y1, x2, y2, z2, tex));
			shapeList.add(Graphics3D.createFloor(x1, y1, x2, y2, z1, tex));
		
		Shape block = new Shape(shapeList);
		
		return block;
	}*/
}