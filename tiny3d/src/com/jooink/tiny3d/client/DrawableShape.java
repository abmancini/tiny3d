package com.jooink.tiny3d.client;

import com.googlecode.gwtwebgl.html.WebGLRenderingContext;

public interface DrawableShape {

	public  void replaceShape(Shape shape);

	public  WebGLRenderingContext getContext();

	public  void draw(float[] perspectiveMatrix);

	public  void resetTime();

	public  boolean isVisible();

	public  void setTransformation(float... tMatrix);
	public  void setBaseTransformation(float... tMatrix);

	public  void rotate(float angle, float... axis);

	public  void translate(float... vec);

	public  void scale(float... s);

	public  void setVisible();

	public  void setNotVisible();
	
	public float[] getMatrix();

}