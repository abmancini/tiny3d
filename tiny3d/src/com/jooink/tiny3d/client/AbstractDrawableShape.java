package com.jooink.tiny3d.client;

import java.util.Date;

import com.google.gwt.typedarrays.shared.Float32Array;
import com.googlecode.gwtwebgl.html.WebGLRenderingContext;

public abstract class AbstractDrawableShape implements DrawableShape {

	private long birthTime = 0;


	protected abstract void draw(WebGLRenderingContext ctx3d, Float32Array mvMatrix, Float32Array perspectiveMatrix,float timeFromCreationInseconds);

	@Override
	public abstract void replaceShape(Shape shape);


	private WebGLRenderingContext ctx3d;

	@Override
	public WebGLRenderingContext getContext() {
		return ctx3d;
	}

	public float[] getMatrix() {
		return matrix;
	}
	
	private float[] matrix ; //transformation matrix (scene default pos 2 actual pos)
	private Float32Array currentMatrix; //ms * t
	private float[] baseTransformaton ; //transformation matrix (scene default pos 2 actual pos)

	public AbstractDrawableShape(WebGLRenderingContext ctx3d) {
		this(ctx3d,null);
	}

	public AbstractDrawableShape(WebGLRenderingContext ctx3d, float... model2SceneMatrix) {
		this.ctx3d = ctx3d;

		if(model2SceneMatrix!= null) 	
			setBaseTransformation(model2SceneMatrix);
		else
			setBaseTransformation(Matrices.identity());



	}


	public void setBaseTransformation(float... model2SceneMatrix) {

		//System.err.println("baseSet");
		this.matrix = model2SceneMatrix;
		baseTransformaton = this.matrix; 
		setupCurrentMatrix();	
	}


	private void setupCurrentMatrix() {
		currentMatrix = Utils.Float32ArrayfromFloatArray(matrix);	
	}


	@Override
	public void draw(float[] perspectiveMatrix) {
		if(birthTime<=0)
			birthTime=(new Date()).getTime();		

		if(ctx3d == null) {
			System.err.println("CTX null ... not drawing");
		} else {	
			draw(ctx3d,currentMatrix, Utils.Float32ArrayfromFloatArray( perspectiveMatrix ), (float)(((double)(birthTime-(new Date()).getTime()))/1000.0d));
		}
	}

	@Override
	public void resetTime() {
		birthTime=0;
	}




	private boolean visible = false;

	@Override
	public boolean isVisible() {
		return visible;
	}	


	@Override
	public void setTransformation(float... tMatrix) {
		this.matrix = Matrices.multiply(tMatrix,baseTransformaton);
		setupCurrentMatrix();
	}

	@Override
	public void rotate(float angle, float... axis) {
		float[] m = this.matrix;
		this.matrix = Matrices.rotate(m,angle,axis);
		setupCurrentMatrix();
	}

	@Override
	public void translate(float... vec) {
		float[] m = this.matrix;
		this.matrix = Matrices.translate(m,vec);
		setupCurrentMatrix();
	}

	@Override
	public void scale(float... s) {
		float[] m = this.matrix;
		this.matrix = Matrices.scale(m,s);
		setupCurrentMatrix();
	}



	@Override
	public void setVisible() {
		this.visible = true;
	}

	@Override
	public void setNotVisible() {
		this.visible = false;
	}

}