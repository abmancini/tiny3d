package com.jooink.tiny3d.client;

import java.util.Map;

import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Uint16Array;


public interface Shape {

	public Float32Array getVerticesArray();

	public Map<String,Uint16Array> getIndexesMap();

	//public int getNumIndices();

	public Float32Array getTextureCoordinatesArray();

	public Float32Array getNormalsArray();

}