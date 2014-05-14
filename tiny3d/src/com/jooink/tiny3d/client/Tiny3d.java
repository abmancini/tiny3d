package com.jooink.tiny3d.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.googlecode.gwtwebgl.client.WebGL;
import com.googlecode.gwtwebgl.html.WebGLRenderingContext;
import com.jooink.tiny3d.client.drawables.SimpleDrawable;
import com.jooink.tiny3d.client.scenes.CleanScene;
import com.jooink.tiny3d.client.shapes.Cube;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tiny3d implements EntryPoint {

	AbstractDrawableShape shape;
	Scene scene;
	
	ToggleButton tb = new ToggleButton("Rotating World", "Rotating Shape");

	
	private com.google.gwt.animation.client.AnimationScheduler.AnimationCallback ac = new AnimationCallback() {
		
		@Override
		public void execute(double timestamp) {
			
			if(tb.isDown())
				shape.rotate(-0.01f, new float[] {0,0,1, 1});
			else
				scene.pushMatrix(Matrices.rotation(-0.01f, new float[] {0,0,1, 1}));
			
			scene.draw();
			AnimationScheduler.get().requestAnimationFrame(ac);
			
		}
	};
	

	
	@Override
	public void onModuleLoad() {
		
		
		
		
		Canvas canvas = Canvas.createIfSupported();
		
		RootPanel.get().add(canvas);
		RootPanel.get().add(tb);
		
		final WebGLRenderingContext ctx = WebGL.getContext(canvas);


		CanvasElement canvasElement =  canvas.getCanvasElement();

		canvasElement.setWidth(640);
		canvasElement.setHeight(480);

		ctx.viewport(0, 0, 640, 480);


		ctx.enable(WebGLRenderingContext.DEPTH_TEST);
		ctx.depthFunc(WebGLRenderingContext.LEQUAL); 

		scene = new CleanScene(ctx, 640, 480);

		final float[] perspectiveMatrix = Matrices.perspectiveMatrix(40,640.0f/480.0f, 0.1f, 1000.0f);
		float[] modelViewMatrix = Matrices.lookaAtMatrix(new float[] {3.5f,3.5f,4.0f} , new float[]{0,0,1},new float[]{0,0,1});

		float[] m = Matrices.multiply(perspectiveMatrix, modelViewMatrix);
		scene.setMatrix(m);

		
		
		
		shape = new SimpleDrawable(ctx,new Cube(), Matrices.scaleMatrix(new float[] {1.0f,1.0f,1.0f,1.0f}));
		
		
		
		scene.addShape(shape);
		
		shape.setVisible();
		
		
		scene.draw();
		
		
		AnimationScheduler.get().requestAnimationFrame(ac);
			
		
		
		
	}
	
	
	
	
}