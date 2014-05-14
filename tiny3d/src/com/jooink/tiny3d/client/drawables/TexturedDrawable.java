package com.jooink.tiny3d.client.drawables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Uint16Array;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtwebgl.html.WebGLBuffer;
import com.googlecode.gwtwebgl.html.WebGLProgram;
import com.googlecode.gwtwebgl.html.WebGLRenderingContext;
import com.googlecode.gwtwebgl.html.WebGLShader;
import com.googlecode.gwtwebgl.html.WebGLTexture;
import com.googlecode.gwtwebgl.html.WebGLUniformLocation;
import com.jooink.tiny3d.client.AbstractDrawableShape;
import com.jooink.tiny3d.client.Shape;
import com.jooink.tiny3d.client.Utils;



public class TexturedDrawable extends AbstractDrawableShape {


	private Shape shape;

	interface TexturingShader extends ClientBundle {
		public static TexturingShader INSTANCE = GWT.create(TexturingShader.class);
		@Source("texturingFragmentShader.txt")
		public TextResource fragmentShader();
		
		@Source("texturingVertexShader.txt")
		public TextResource vertexShader();
	}

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

	private boolean textureLoaded = false;

	private WebGLTexture gl_texture;


	private int vertexPositionAttribute;
	private int textureCoordAttribute;
	private WebGLProgram program;
	private WebGLUniformLocation pMatrixUniform;
	private WebGLUniformLocation mvMatrixUniform;
	private WebGLUniformLocation samplerUniform;

	private WebGLBuffer cubeVertexPositionBuffer;
	private WebGLBuffer cubeVertexTextureCoordBuffer;
	//private WebGLBuffer cubeVertexIndexBuffer;



	public interface  Resource extends ClientBundle {
		//@Source("marker.gif")
		@Source("AppDays2014.png")
		ImageResource tile();
	}

	private static Resource resource;

	private static Resource getDefaultResource() {
		if(resource == null)
			resource = GWT.create(Resource.class);
		return resource;
	}




	public TexturedDrawable(WebGLRenderingContext ctx, Shape shape) {
		this(ctx, shape, getDefaultResource().tile(), null);
	}

	public TexturedDrawable(WebGLRenderingContext ctx, Shape shape,float[] mat) {
		this(ctx, shape, getDefaultResource().tile(), mat);
	}

	public TexturedDrawable(WebGLRenderingContext ctx, Shape shape, ImageResource tile) {
		this(ctx, shape,tile, null);
	}


	private ImageResource tile;
	public TexturedDrawable(WebGLRenderingContext ctx,  Shape shape, ImageResource tile, float[] mat) {
		super(ctx,mat);
		this.tile = tile;
		init(shape);
	}



	protected void init(Shape shape) {
		this.shape = shape;
		if(getContext() != null) {
			prepareShaders(getContext());
			prepareBuffers(getContext());
			prepareTexture(getContext(),tile);
		} else {
			System.err.println("not preparing shape having ctx null");
		}
	}




	@Override
	public void draw(WebGLRenderingContext ctx3d, Float32Array modelViewMatrix, Float32Array perspectiveMatrix, float unusedTime) {

		if(!textureLoaded)
			return;

		ctx3d.useProgram(program);

		ctx3d.enableVertexAttribArray(vertexPositionAttribute);  
		ctx3d.enableVertexAttribArray(textureCoordAttribute);
		
		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexPositionBuffer);
		ctx3d.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);

		ctx3d.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, cubeVertexTextureCoordBuffer);
		ctx3d.vertexAttribPointer(textureCoordAttribute, 2, WebGLRenderingContext.FLOAT, false, 0, 0);


		//Set slot 0 as the active Texture
		ctx3d.activeTexture(WebGLRenderingContext.TEXTURE0);

		ctx3d.bindTexture(WebGLRenderingContext.TEXTURE_2D, this.gl_texture);
		//Update The Texture Sampler in the fragment shader to use slot 0
		ctx3d.uniform1i(samplerUniform, 0);





		//ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, cubeVertexIndexBuffer);

		ctx3d.uniformMatrix4fv(pMatrixUniform, false, perspectiveMatrix);
		ctx3d.uniformMatrix4fv(mvMatrixUniform, false,  modelViewMatrix);


		for(ShapePartDrawData part : shapeParts ) {
			int numIndices = part.numIndices;

			//indexes (aka 'faces')
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, part.shapeVertexIndexBuffer);
			ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, numIndices, WebGLRenderingContext.UNSIGNED_SHORT, 0);
		}

		//ctx3d.drawElements(WebGLRenderingContext.TRIANGLES, TexturedDrawable.getNumIndices(), WebGLRenderingContext.UNSIGNED_SHORT, 0);


		ctx3d.disableVertexAttribArray(vertexPositionAttribute);  
		ctx3d.disableVertexAttribArray(textureCoordAttribute);



	}


	private void prepareShaders(WebGLRenderingContext ctx3d) {
		//create shaders

		//Window.alert("vertexSh: "+Shaders.INSTANCE.vertexShader3D().getText());
		WebGLShader vs  = Utils.createShader(WebGLRenderingContext.VERTEX_SHADER, TexturingShader.INSTANCE.vertexShader().getText(), ctx3d);

		//Window.alert("fragmentSh: "+Shaders.INSTANCE.fragmentShader3D().getText());
		//WebGLShader fs = Utilities.createShader(WebGLRenderingContext.FRAGMENT_SHADER, Shaders.INSTANCE.fragmentShader3D().getText(), ctx3d);
		WebGLShader fs = Utils.createShader(WebGLRenderingContext.FRAGMENT_SHADER, TexturingShader.INSTANCE.fragmentShader().getText(), ctx3d);

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
		this.samplerUniform = ctx3d.getUniformLocation(program, "uSampler");

		ctx3d.disableVertexAttribArray(vertexPositionAttribute);  
		ctx3d.disableVertexAttribArray(textureCoordAttribute);

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



		//cubeVertexIndexBuffer = ctx3d.createBuffer();
		//ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, cubeVertexIndexBuffer);
		//ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, TexturedDrawable.getIndexesArray(), WebGLRenderingContext.STATIC_DRAW);


		for(Entry<String, Uint16Array> part : shape.getIndexesMap().entrySet()) {	
			WebGLBuffer buffer = ctx3d.createBuffer();
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, buffer);
			Uint16Array idxs = part.getValue();
			ctx3d.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, idxs , WebGLRenderingContext.STATIC_DRAW);
			shapeParts.add(new ShapePartDrawData(buffer, idxs.length()));
			ctx3d.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, null);
		}


	}



	private void prepareTexture(final WebGLRenderingContext ctx3d, ImageResource tile) {

		//Create a new Texture and Assign it as the active one  
		gl_texture = ctx3d.createTexture();  

		final Image img = new Image();
		img.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		RootPanel.get().add(img);
		img.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				//image loaded
				RootPanel.get().remove(img);
				onTextureLoaded(img,ctx3d);
			}
		});

		img.setResource(tile);

	}

	private void onTextureLoaded(Image img, WebGLRenderingContext ctx3d) {

		//bind the texture  
		ctx3d.bindTexture(WebGLRenderingContext.TEXTURE_2D, gl_texture);

		//Flip Positive Y (Optional)  
		ctx3d.pixelStorei(WebGLRenderingContext.UNPACK_FLIP_Y_WEBGL, 1);  

		//Load in The Image  
		ctx3d.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, ImageElement.as(img.getElement()));    

		//Setup Scaling properties  
		//		ctx3d.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);    
		//		ctx3d.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_NEAREST);    
		//		ctx3d.generateMipmap(WebGLRenderingContext.TEXTURE_2D);   


		ctx3d.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.NEAREST);    
		ctx3d.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.NEAREST);    

		//Unbind the texture  
		ctx3d.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);  

		textureLoaded = true;
	}

	@Override
	public void replaceShape(Shape shape) {
		init(shape);
	}

}
