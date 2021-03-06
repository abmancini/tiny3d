package com.jooink.tiny3d.client.shapes;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.typedarrays.client.Uint16ArrayNative;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Uint16Array;
import com.jooink.tiny3d.client.Shape;

public class UnitCube implements Shape {

	//data
	private static final float[] vertices = { 
			// Front face
			-0.5f, -0.5f,  0.5f,
			0.5f,  -0.5f,  0.5f,
			0.5f,   0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,

			// Back face
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			0.5f,   0.5f, -0.5f,
			0.5f,  -0.5f, -0.5f,

			// Top face
			-0.5f,  0.5f, -0.5f,
			-0.5f,  0.5f,  0.5f,
			0.5f,   0.5f,  0.5f,
			0.5f,   0.5f, -0.5f,

			// Bottom face
			-0.5f,  -0.5f, -0.5f,
			0.5f,   -0.5f, -0.5f,
			0.5f,   -0.5f,  0.5f,
			-0.5f,  -0.5f,  0.5f,

			// Right face
			0.5f,  -0.5f, -0.5f,
			0.5f,   0.5f, -0.5f,
			0.5f,   0.5f,  0.5f,
			0.5f,  -0.5f,  0.5f,

			// Left face
			-0.5f,  -0.5f, -0.5f,
			-0.5f,  -0.5f,  0.5f,
			-0.5f,   0.5f,  0.5f,
			-0.5f,   0.5f, -0.5f
	};

	private static final float[] normals = { 
		// Front face
		0.0f, 0.0f,  1.0f,
		0.0f, 0.0f,  1.0f,
		0.0f, 0.0f,  1.0f,
		0.0f, 0.0f,  1.0f,

		// Back face
		0.0f, 0.0f, -1.0f,
		0.0f, 0.0f, -1.0f,
		0.0f, 0.0f, -1.0f,
		0.0f, 0.0f, -1.0f,

		// Top face
		0.0f,  1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,

		// Bottom face
		0.0f,  -1.0f, 0.0f,
		0.0f,  -1.0f, 0.0f,
		0.0f,  -1.0f, 0.0f,
		0.0f,  -1.0f, 0.0f,

		// Right face
		1.0f,  0.0f, 0.0f,
		1.0f,  0.0f, 0.0f,
		1.0f,  0.0f, 0.0f,
		1.0f,  0.0f, 0.0f,

		// Left face
		-1.0f,  0.0f, 0.0f,
		-1.0f,  0.0f, 0.0f,
		-1.0f,  0.0f, 0.0f,
		-1.0f,  0.0f, 0.0f
};

	private static final int[] triangles = {
			0, 1, 2,      0, 2, 3,    // Front face
			4, 5, 6,      4, 6, 7,    // Back face
			8, 9, 10,     8, 10, 11,  // Top face
			12, 13, 14,   12, 14, 15, // Bottom face
			16, 17, 18,   16, 18, 19, // Right face
			20, 21, 22,   20, 22, 23  // Left face
			//
			//
	};

	private static final float[] texture_coords = {
			////
			// Front face
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,

			// Back face
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,

			// Top face
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,

			// Bottom face
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,

			// Right face
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,

			// Left face
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f
	};


	
	@Override
	public Float32Array getVerticesArray() {
		Float32Array a = Float32ArrayNative.create(vertices.length);
		a.set(vertices);
		return a;
	}

	
	@Override
	public Map<String,Uint16Array> getIndexesMap() {
		Uint16Array a = Uint16ArrayNative.create(triangles.length);
		a.set(triangles);
		HashMap<String, Uint16Array> m = new HashMap<String,Uint16Array>();
		m.put("default", a);
		return m;
	}

//	@Override
//	public Uint16Array getIndexesArray() {
//		Uint16Array a = Uint16ArrayNative.create(triangles.length);
//		a.set(triangles);
//		return a;
//	}

//	@Override
//	public int getNumIndices() {
//		return triangles.length;
//	}

	@Override
	public Float32Array getTextureCoordinatesArray() {
		Float32Array a = Float32ArrayNative.create(texture_coords.length);
		a.set(texture_coords);
		return a;
	}

	@Override
	public Float32Array getNormalsArray() {
		Float32Array a = Float32ArrayNative.create(normals.length);
		a.set(normals);
		return a;
	}

}
