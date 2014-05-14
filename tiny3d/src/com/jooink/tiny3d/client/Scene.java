package com.jooink.tiny3d.client;

import java.util.ArrayList;

import com.googlecode.gwtwebgl.html.WebGLRenderingContext;


public abstract class Scene {


	public interface ShapeRegistrationHandler {
		public void remove();
	}


	protected ArrayList<DrawableShape> drawableShapes = new ArrayList<DrawableShape>();
	
	protected float[] matrix;
	private WebGLRenderingContext ctx;

	public Scene(WebGLRenderingContext ctx) {
		this.ctx = ctx;
	}


	public final void setMatrix(float[] p) {
		this.matrix = p;
	}

	public final float[] pushMatrix(float[] p) {
		float[] oldMatrix = this.matrix; 
		this.matrix = Matrices.multiply(this.matrix, p); 
		return oldMatrix;
	}


	public final float[] getMatrix() {
		return this.matrix;
	}


	public final ShapeRegistrationHandler addShape(final DrawableShape s) {
		drawableShapes.add(s);
		return new ShapeRegistrationHandler() {	
			@Override
			public void remove() {
				drawableShapes.remove(s);
			}
		};
	}

	
	//remove all the shapes
	public void removeShapes() {
		drawableShapes.clear();
	}



	/*
	 * SHOULD call drawShapes();
	 */
	public abstract void draw();
	
	
	protected void drawShapes() {
		for(DrawableShape s : drawableShapes) {
			if(s.isVisible())
				s.draw(matrix);
		}
	}

	
	
	public void resize(int width, int height) {
		//			had_resize=true;
		//			this.w = w;
		//			this.h = h;
		//		
		//		ctx3d.useProgram(program);
		//		ctx3d.viewport(0, 0, w, h);
		//		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vertexPositionBuffer);
		//		ArrayBufferView rect = createRectangleVertices( 0, 0,w,h);
		//		ctx3d.bufferData(WebGLRenderingContext.ARRAY_BUFFER, rect , WebGLRenderingContext.STATIC_DRAW);

	}




	public final WebGLRenderingContext getContext() {
		return ctx;
	}

}