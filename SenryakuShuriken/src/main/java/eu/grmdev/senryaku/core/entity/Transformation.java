package eu.grmdev.senryaku.core.entity;

import static org.lwjgl.opengl.GL11.*;

public class Transformation {
	private float[] pos = new float[3];
	private float[] scale = new float[]{1f, 1f, 1f};
	private float[] rot = new float[3];
	
	public Transformation() {}
	
	public Transformation(float x, float y, float z) {
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
	}
	
	public void transform() {
		glTranslatef(pos[0], pos[1], pos[2]);
		glRotatef(rot[0], 1f, 0f, 0f);
		glRotatef(rot[1], 0f, 1f, 0f);
		glRotatef(rot[2], 0f, 0f, 1f);
		glScalef(scale[0], scale[1], scale[2]);
	}
	
	public void move(float x, float y, float z) {
		pos[0] += x;
		pos[1] += y;
		pos[2] += z;
	}
	
	public void resize(float sx, float sy, float sz) {
		scale[0] *= sx;
		scale[1] *= sy;
		scale[2] *= sz;
	}
	
	public void rotate(float rx, float ry, float rz) {
		rot[0] += rx;
		rot[1] += ry;
		rot[2] += rz;
	}
}
