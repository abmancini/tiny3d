package com.jooink.tiny3d.client.drawables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Uint16Array;
import com.googlecode.gwtwebgl.html.WebGLBuffer;
import com.googlecode.gwtwebgl.html.WebGLProgram;
import com.googlecode.gwtwebgl.html.WebGLRenderingContext;
import com.googlecode.gwtwebgl.html.WebGLShader;
import com.googlecode.gwtwebgl.html.WebGLUniformLocation;
import com.jooink.tiny3d.client.AbstractDrawableShape;
import com.jooink.tiny3d.client.Shape;
import com.jooink.tiny3d.client.Utils;



public class FlowerDrawable  extends AbstractDrawableShape  {

	private Shape shape;


	//we need a list of data needed to 
	//draw a part of a shape:
	// WebGLBuffer containing indexes, 
	// the number of indexes,
	private class ShapePartDrawData {
		WebGLBuffer shapeVertexIndexBuffer;
		int numIndices;
		public ShapePartDrawData(WebGLBuffer shapeVertexIndexBuffer, int numIndices) {
			this.shapeVertexIndexBuffer = shapeVertexIndexBuffer;
			this.numIndices = numIndices;
		}

	}

	//private WebGLBuffer shapeVertexIndexBuffer;
	private List<ShapePartDrawData> shapeParts = new ArrayList<ShapePartDrawData>();


	interface FlowerShader extends ClientBundle {
		public static FlowerShader INSTANCE = GWT.create(FlowerShader.class);
		@Source("flowerFragmentShader.txt")
		public TextResource flowerShader();
		@Source("flowerVertexShader.txt")
		public TextResource flowerVertexShader();
	}

	public FlowerDrawable(WebGLRenderingContext ctx, Shape shape, float[] mat) {
		super(ctx,mat);	
		init(shape);
	}


	public void replaceShape(Shape shape) {
		init(shape);
	}

	protected void init(Shape shape) {
		this.shape = shape;
		if(getContext() != null) {
			prepareShaders(getContext());
			prepareBuffers(getContext());
		} else {
			System.err.println("not preparing shape having ctx null");
		}
	}




	private int vertexPositionAttribute;
	private int textureCoordAttribute;
	private WebGLProgram program;
	private WebGLUniformLocation pMatrixUniform;
	private WebGLUniformLocation mvMatrixUniform;

	private WebGLUniformLocation timeUniform;


	private WebGLBuffer cubeVertexPositionBuffer;
	private WebGLBuffer cubeVertexTextureCoordBuffer;
	//private WebGLBuffer cubeVertexIndexBuffer;



	@Override
	public void draw(WebGLRenderingContext ctx3d, Float32Array modelViewMatrix, Float32Array perspectiveMatrix, float timeFromCreationInseconds) {

		//System.err.println("drawing");
		ctx3d.useProgram(program);


		ctx3d.uniform1f(timeUniform,timeFromCreationInseconds/1.0f);


		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexPositionBuffer);
		ctx3d.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);

		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexTextureCoordBuffer);
		ctx3d.vertexAttribPointer(textureCoordAttribute, 2, WebGLRenderingContext.FLOAT, false, 0, 0);


		ctx3d.uniformMatrix4fv(pMatrixUniform, false, perspectiveMatrix );
		ctx3d.uniformMatrix4fv(mvMatrixUniform, false,  modelViewMatrix );

		//ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, cubeVertexIndexBuffer);
		//ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, shape.getNumIndices(), WebGLRenderingContext.UNSIGNED_SHORT, 0);

		for(ShapePartDrawData part : shapeParts ) {
			int numIndices = part.numIndices;

			//indexes (aka 'faces')
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, part.shapeVertexIndexBuffer);
			ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, numIndices, WebGLRenderingContext.UNSIGNED_SHORT, 0);
		}



		//ctx3d.disableVertexAttribArray(vertexPositionAttribute);  
		//ctx3d.disableVertexAttribArray(vertexNormalAttribute);



	}


	private void prepareShaders(WebGLRenderingContext ctx3d) {
		//create shaders

		//Window.alert("vertexSh: "+Shaders.INSTANCE.vertexShader3D().getText());
		WebGLShader vs  = Utils.createShader(WebGLRenderingContext.VERTEX_SHADER, FlowerShader.INSTANCE.flowerVertexShader().getText(), ctx3d);

		//Window.alert("fragmentSh: "+Shaders.INSTANCE.fragmentShader3D().getText());
		//WebGLShader fs = Utils.createShader(WebGLRenderingContext.FRAGMENT_SHADER, Shaders.INSTANCE.fragmentShader3D().getText(), ctx3d);
		WebGLShader fs = Utils.createShader(WebGLRenderingContext.FRAGMENT_SHADER, FlowerShader.INSTANCE.flowerShader().getText(), ctx3d);

		//create program
		this.program = Utils.createAndUseProgram(Arrays.asList(vs,fs), ctx3d);


		//Link Vertex Position Attribute from Shader  
		this.vertexPositionAttribute = ctx3d.getAttribLocation(program, "aVertexPosition");  
		ctx3d.enableVertexAttribArray(vertexPositionAttribute);  

		//Link Texture Coordinate Attribute from Shader  
		this.textureCoordAttribute = ctx3d.getAttribLocation(program, "aTextureCoord");  
		ctx3d.enableVertexAttribArray(textureCoordAttribute);

		this.pMatrixUniform = ctx3d.getUniformLocation(program, "uPMatrix");
		this.mvMatrixUniform = ctx3d.getUniformLocation(program, "uMVMatrix");
		//	this.samplerUniform = ctx3d.getUniformLocation(program, "uSampler");


		this.timeUniform = ctx3d.getUniformLocation(program, "time");
	}


	private void prepareBuffers(WebGLRenderingContext ctx3d) {

		//Create a New Buffer
		cubeVertexPositionBuffer = ctx3d.createBuffer();
		//Bind it as The Current Buffer
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexPositionBuffer);
		// Fill it With the Data

		//Window.alert("vert RETURNTED Buf len:" + ab.getByteLength());
		ctx3d.bufferData(WebGLRenderingContext.ARRAY_BUFFER, shape.getVerticesArray(), WebGLRenderingContext.STATIC_DRAW);

		//Connect Buffer To Shader's attribute





		cubeVertexTextureCoordBuffer = ctx3d.createBuffer();
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexTextureCoordBuffer);
		ctx3d.bufferData(WebGLRenderingContext.ARRAY_BUFFER, shape.getTextureCoordinatesArray(), WebGLRenderingContext.STATIC_DRAW);



		//		cubeVertexIndexBuffer = ctx3d.createBuffer();
		//		ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, cubeVertexIndexBuffer);
		//		ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, shape.getIndexesArray(), WebGLRenderingContext.STATIC_DRAW);


		for(Entry<String, Uint16Array> part : shape.getIndexesMap().entrySet()) {	
			WebGLBuffer buffer = ctx3d.createBuffer();
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, buffer);
			Uint16Array idxs = part.getValue();
			ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, idxs , WebGLRenderingContext.STATIC_DRAW);
			shapeParts.add(new ShapePartDrawData(buffer, idxs.length()));
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, null);
		}


	}



}
