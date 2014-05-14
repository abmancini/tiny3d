package com.jooink.tiny3d.client.drawables;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

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

public class SimpleDrawable extends AbstractDrawableShape  {


	private Shape shape;
	//private int numIndices;

	private WebGLProgram program;
	private int vertexPositionAttribute;
	//private int textureCoordAttribute;
	private int vertexNormalAttribute;

	private WebGLUniformLocation pMatrixUniform;
	private WebGLUniformLocation mvMatrixUniform;
	private WebGLUniformLocation nmMatrixUniform;
	private WebGLUniformLocation colorUniform;
	private WebGLUniformLocation ambientColorUniform;
	private WebGLUniformLocation lightColorUniform;
	private WebGLUniformLocation lightDirectionUniform;



	private WebGLBuffer shapeVertexPositionBuffer;
	private WebGLBuffer verticesNormalsBuffer;


	//we need a list of data needed to 
	//draw a part of a shape:
	// WebGLBuffer containing indexes, 
	// the number of indexes,
	// the color & ambient color
	private class ShapePartDrawData {
		WebGLBuffer shapeVertexIndexBuffer;
		int numIndices;
		float[] color;
		float[] ambientColor;
		public ShapePartDrawData(WebGLBuffer shapeVertexIndexBuffer,
				int numIndices, float[] color, float[] ambientColor) {
			this.shapeVertexIndexBuffer = shapeVertexIndexBuffer;
			this.numIndices = numIndices;
			this.color = color;
			this.ambientColor = ambientColor;
		}

	}

	//private WebGLBuffer shapeVertexIndexBuffer;
	private List<ShapePartDrawData> shapeParts = new ArrayList<SimpleDrawable.ShapePartDrawData>();



	private float[] color = new float[] {.0f,.6f,.6f,1f };
	private float[] ambientColor = new float[] { .2f, .2f, .2f };
	private float[] lightColor= new float[]{ 0.8f, 0.8f, 0.8f };
	private float[] lightDirection = new float[] {0.0f, 1.0f/(float)Math.sqrt(2.0), 1.0f/(float)Math.sqrt(2.0)};



	public float[] getColor() {
		return color;
	}

	public void setColor(float r,float g, float b, float a) {
		this.color = new float[] {r,g,b,a};
	}

	public float[] getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(float r,float g, float b) {
		this.ambientColor = new float[] {r,g,b};
	}

	public float[] getLightColor() {
		return lightColor;
	}

	public void setLightColor(float r,float g, float b) {
		this.lightColor = new float[] {r,g,b};
	}

	public float[] getLightDirection() {
		return lightDirection;
	}

	public void setLightDirection(float x,float y, float z) {

		float r = (float)Math.sqrt(x*x + y*y + z*z);

		this.lightDirection = new float[] {x/r,y/r,z/r};
	}




	public interface HasMaterialsByName {
		public SimpleMaterial get(String name);
	}


	static private class EmptyMaterials implements HasMaterialsByName {
		@Override
		public SimpleMaterial get(String name) {
			return null;
		}

	}

	private HasMaterialsByName materials;

	public SimpleDrawable(WebGLRenderingContext ctx, Shape shape, float[] mat) {
		this(ctx,shape,mat,new EmptyMaterials());
	}


	public SimpleDrawable(WebGLRenderingContext ctx, Shape shape, float[] mat, HasMaterialsByName materials) {
		super(ctx,mat);	
		this.materials = materials;
		init(shape);
	}

	public void replaceShape(Shape shape, HasMaterialsByName materials) {
		this.materials = materials;
		init(shape);
	}
	//keep materials
	public void replaceShape(Shape shape) {
		replaceShape(shape,this.materials);
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


	public static  class SimpleMaterial {
		private float[] color;
		private float[] ambientColor;

		public float[] getColor() {
			return color;
		}

		public float[] getAmbientColor() {
			return ambientColor;
		}

		public SimpleMaterial(float[] color, float[] ambientColor) {
			super();
			this.color = color;
			this.ambientColor = ambientColor;
		}

	}


	private void prepareShaders(WebGLRenderingContext ctx3d) {
		//create shaders

		WebGLShader vs  = Utils.createShader(WebGLRenderingContext.VERTEX_SHADER, Shaders.INSTANCE.vertexShader().getText(), ctx3d);
		WebGLShader fs = Utils.createShader(WebGLRenderingContext.FRAGMENT_SHADER, Shaders.INSTANCE.fragmentShader().getText(), ctx3d);

		//create program
		this.program = Utils.createAndUseProgram(Arrays.asList(vs,fs), ctx3d);

		//Link Vertex Position Attribute from Shader  
		this.vertexPositionAttribute = ctx3d.getAttribLocation(program, "aVertexPosition");  

		//Link Normal Vector Attribute from Shader  
		this.vertexNormalAttribute = ctx3d.getAttribLocation(program, "aVertexNormal"); 

		this.pMatrixUniform = ctx3d.getUniformLocation(program, "uPMatrix");
		this.mvMatrixUniform = ctx3d.getUniformLocation(program, "uMVMatrix");
		this.nmMatrixUniform = ctx3d.getUniformLocation(program, "uNormalMatrix");        
		this.colorUniform = ctx3d.getUniformLocation(program, "uColor");


		this.ambientColorUniform = ctx3d.getUniformLocation(program, "uAmbientLight");
		this.lightColorUniform = ctx3d.getUniformLocation(program, "uLightColor");
		this.lightDirectionUniform = ctx3d.getUniformLocation(program, "uLightDirection");

	}


	private void prepareBuffers(WebGLRenderingContext ctx3d) {


		shapeVertexPositionBuffer = ctx3d.createBuffer();
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, shapeVertexPositionBuffer);
		ctx3d.bufferData(WebGLRenderingContext.ARRAY_BUFFER, shape.getVerticesArray(), WebGLRenderingContext.STATIC_DRAW);

		verticesNormalsBuffer = ctx3d.createBuffer();
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,verticesNormalsBuffer);
		ctx3d.bufferData(WebGLRenderingContext.ARRAY_BUFFER, shape.getNormalsArray(), WebGLRenderingContext.STATIC_DRAW);



		for(Entry<String, Uint16Array> part : shape.getIndexesMap().entrySet()) {	
			WebGLBuffer buffer = ctx3d.createBuffer();
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, buffer);
			Uint16Array idxs = part.getValue();
			ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, idxs , WebGLRenderingContext.STATIC_DRAW);

			//if a matching material has been provided
			SimpleMaterial material = materials.get(part.getKey());
			if(material == null) { //use default 
				shapeParts.add(new ShapePartDrawData(buffer, idxs.length(), color, ambientColor));
				System.err.println(part.getKey());
			} else 
				shapeParts.add(new ShapePartDrawData(buffer, idxs.length(), material.getColor(), material.getAmbientColor()));

			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, null);
		}


	}



	@Override
	public void draw(WebGLRenderingContext ctx3d, Float32Array modelViewMatrix, Float32Array perspectiveMatrix, float unusedTime) {




		//Window.alert("ecchime");

		ctx3d.enableVertexAttribArray(vertexPositionAttribute);  
		ctx3d.enableVertexAttribArray(vertexNormalAttribute);


		ctx3d.useProgram(program);

		//positions
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, shapeVertexPositionBuffer);
		ctx3d.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);

		//normals
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, verticesNormalsBuffer);
		ctx3d.vertexAttribPointer(vertexNormalAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);        


		//uniforms
		ctx3d.uniformMatrix4fv(pMatrixUniform, false, perspectiveMatrix);

		ctx3d.uniformMatrix4fv(mvMatrixUniform, false,  modelViewMatrix);
		ctx3d.uniformMatrix4fv(nmMatrixUniform, false, modelViewMatrix);  // mmm 



		ctx3d.uniform3f(lightColorUniform, lightColor[0], lightColor[1], lightColor[2]);
		ctx3d.uniform3f(lightDirectionUniform, lightDirection[0], lightDirection[1], lightDirection[2]);

		//		int i=0;	
		//		for(  Entry<String, Uint16Array> part : shape.getIndexesMap().entrySet()) {	
		//			i++;
		//			ctx3d.uniform4f(colorUniform, color[0] * i%2, color[1] * i%3, color[2], color[3]);
		//			ctx3d.uniform3f(ambientColorUniform,  ambientColor[0], ambientColor[1], ambientColor[2]);
		//
		//			//System.err.println(part.getKey());
		//			int numIndices = part.getValue().length();
		//
		//			//indexes (aka 'faces')
		//			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, shapeVertexIndexBuffer);
		//
		//			//cache THIS in prepareBuffers
		//			ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, part.getValue(), WebGLRenderingContext.STATIC_DRAW);
		//
		//			ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, numIndices, WebGLRenderingContext.UNSIGNED_SHORT, 0);
		//			//XXX
		//		}


		for(ShapePartDrawData part : shapeParts ) {
			float[] c = part.color;
			float[] ac = part.ambientColor;

			ctx3d.uniform4f(colorUniform, c[0], c[1], c[2], c[3]);
			ctx3d.uniform3f(ambientColorUniform,  ac[0], ac[1], ac[2]);

			//System.err.println(part.getKey());
			int numIndices = part.numIndices;

			//indexes (aka 'faces')
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, part.shapeVertexIndexBuffer);

			ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, numIndices, WebGLRenderingContext.UNSIGNED_SHORT, 0);

		}



		ctx3d.disableVertexAttribArray(vertexPositionAttribute);  
		ctx3d.disableVertexAttribArray(vertexNormalAttribute);


		//Window.alert("fine");


	}












}
