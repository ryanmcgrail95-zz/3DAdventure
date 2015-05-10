package dt;

import func.Math2D;

public class vec4 {
	private float array[];
	
	
	public vec4() {
		array = new float[4];
		
		array[0] = 0;
		array[1] = 0;
		array[2] = 0;
		array[3] = 1;
	}
	
	public vec4(float a, float b, float c, float d) {
		array = new float[4];
		
		array[0] = a;
		array[1] = b;
		array[2] = c;
		array[3] = d;
	}

	public vec4(vec4 other) {
		array = new float[4];
		
		set(other);
	}
	
	public void set(int index, float value) {
		if(index < 0 || index > 3)
			return;
		else
			array[index] = value;
	}
	
	public void set(vec4 other) {
		for(int i = 0; i < 4; i++)
			array[i] = other.get(i);
	}

	public float get(int index) {
		if(index < 0 || index > 3)
			return 0;
		else
			return array[index];
	}
	
	public float len3() {
		return Math2D.calcLen(get(0),get(1),get(2));
	}
	
	public float len() {
		return Math2D.calcLen(get(0),get(1),get(2),get(3));
	}
	
	public vec4 copy() {
		return new vec4(this);
	}
	
	public vec4 norm() {
		float len = len();

		return new vec4(get(0)/len,get(1)/len,get(2)/len,get(3)/len);
	}
	
	public vec4 norm3() {
		float len = len3();

		return new vec4(get(0)/len,get(1)/len,get(2)/len,get(3));
	}
	
	// Operators
	public vec4 add(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 4; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	
	public vec4 mult(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 4; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	
	public float dot(vec4 other) {
		float value = 0;
		
		for(int i = 0; i < 4; i++)
			value += array[i]*other.array[i];
		
		return value;
	}
	

	public vec4 add3(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	
	public vec4 mult(mat4 other) {
		vec4 newV = new vec4();
				
		for(int r = 0; r < 4; r++)
			newV.set(r, other.getCol(r).dot(this));
		
		return newV;
	}
	
	public vec4 mult3(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	
	public vec4 mult3(float num) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]*num);
		
		return newV;
	}
	
	public float dot3(vec4 other) {
		float value = 0;
		
		for(int i = 0; i < 3; i++)
			value += array[i]*other.array[i];
		
		return value;
	}
	
	public vec4 cross3(vec4 other) {
		vec4 newV = new vec4();
		
		newV.set(0, array[1]*other.array[2] - array[2]*other.array[1]);		
		newV.set(1, array[2]*other.array[0] - array[0]*other.array[2]);
		newV.set(2, array[0]*other.array[1] - array[1]*other.array[0]);
		
		return newV;
	}
	

	public void print() {
		for(int i = 0; i < 4; i++) {
			if(i != 0)
				System.out.print(' ');
			System.out.print(get(i));
		}
	};
	public void println() {
		print();
		System.out.print("\n");
	}
}