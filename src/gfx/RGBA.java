package gfx;

public class RGBA {
	private float R;
	private float G;
	private float B;
	private float A;
	
	public RGBA(float R, float G, float B, float A) {
		this.R = R;
		this.G = G;
		this.B = B;
		this.A = A;
	}
	
	public float getR() {
		return R;
	}	
	public float getG() {
		return G;
	}
	public float getB() {
		return G;
	}
	public float getA() {
		return A;
	}
	
	public float getValue() {
		return (float) (R*.27 + G*.71 + B*.07);
	}
	
	public void invert() {
		R = 1-R;
		G = 1-G;
		B = 1-B;
	}
}
