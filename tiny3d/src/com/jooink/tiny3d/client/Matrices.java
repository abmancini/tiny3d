package com.jooink.tiny3d.client;


//QnD js->java from from https://github.com/toji/gl-matrix

public class Matrices {
	/*
	 * float[] perspective
	 * Generates a perspective projection matrix with the given bounds
	 *
	 * Params:
	 * fovy - scalar, vertical field of view
	 * aspect - scalar, aspect ratio. typically viewport width/height
	 * near, far - scalar, near and far bounds of the frustum
	 * dest - Optional, mat4 frustum matrix will be written into
	 *
	 * Returns:
	 * dest if specified, a new mat4 otherwise
	 */
	public static float[] 
	                     perspectiveMatrix(float fovy, float aspect, 
	                    		 float near, float far) {
		float top =(float) (near*Math.tan(fovy*Math.PI / 360.0));
		float right = top*aspect;
		return frustumMatrix(-right, right, -top, top, near, far);
	};

	/*
	 * float[] frustum
	 * Generates a frustum matrix with the given bounds
	 *
	 * Params:
	 * left, right - scalar, left and right bounds of the frustum
	 * bottom, top - scalar, bottom and top bounds of the frustum
	 * near, far - scalar, near and far bounds of the frustum
	 * dest - Optional, mat4 frustum matrix will be written into
	 *
	 * Returns:
	 * dest if specified, a new mat4 otherwise
	 */
	public static float[] 
	                     frustumMatrix(float left, float right, 
	                    		 float bottom, float top, 
	                    		 float near, float far) {

		float dest[] = new float[16];
		float rl = (right - left);
		float tb = (top - bottom);
		float fn = (far - near);
		dest[0] = (near*2.0f) / rl;
		dest[1] = 0.0f;
		dest[2] = 0.0f;
		dest[3] = 0.0f;
		dest[4] = 0.0f;
		dest[5] = (near*2.0f) / tb;
		dest[6] = 0.0f;
		dest[7] = 0.0f;
		dest[8] = (right + left) / rl;
		dest[9] = (top + bottom) / tb;
		dest[10] = -(far + near) / fn;
		dest[11] = -1.0f;
		dest[12] = 0.0f;
		dest[13] = 0.0f;
		dest[14] = -(far*near*2.0f) / fn;
		dest[15] = 0.0f;
		return dest;
	}



	/*
	 * float[] lookAt
	 * Generates a look-at matrix with the given eye position, focal point, and up axis
	 *
	 * Params:
	 * eye - vec3, position of the viewer
	 * center - vec3, point the viewer is looking at
	 * up - vec3 pointing "up"
	 * dest - Optional, mat4 frustum matrix will be written into
	 *
	 * Returns:
	 * dest if specified, a new mat4 otherwise
	 */

	public static  float[] lookaAtMatrix(float[] eye, float[] center, float[] up) {
		float eyex = eye[0];
		float eyey = eye[1];
		float eyez = eye[2];
		float upx = up[0];
		float upy = up[1];
		float upz = up[2];

		float z0,z1,z2,x0,x1,x2,y0,y1,y2,len;

		//vec3.direction(eye, center, z);
		z0 = eyex - center[0];
		z1 = eyey - center[1];
		z2 = eyez - center[2];

		// normalize (no check needed for 0 because of early return)
		len = (float)(1.0/Math.sqrt(z0*z0 + z1*z1 + z2*z2));
		z0 *= len;
		z1 *= len;
		z2 *= len;

		//vec3.normalize(vec3.cross(up, z, x));
		x0 = upy*z2 - upz*z1;
		x1 = upz*z0 - upx*z2;
		x2 = upx*z1 - upy*z0;
		len = (float)(Math.sqrt(x0*x0 + x1*x1 + x2*x2));
		if (len==0) {
			x0 = 0;
			x1 = 0;
			x2 = 0;
		} else {
			len = 1.0f/len;
			x0 *= len;
			x1 *= len;
			x2 *= len;
		};

		//vec3.normalize(vec3.cross(z, x, y));
		y0 = z1*x2 - z2*x1;
		y1 = z2*x0 - z0*x2;
		y2 = z0*x1 - z1*x0;

		len = (float)Math.sqrt(y0*y0 + y1*y1 + y2*y2);
		if (len==0) {
			y0 = 0;
			y1 = 0;
			y2 = 0;
		} else {
			len = 1.0f/len;
			y0 *= len;
			y1 *= len;
			y2 *= len;
		}

		float[] dest = new float[16];
		dest[0] = x0;
		dest[1] = y0;
		dest[2] = z0;
		dest[3] = 0.0f;
		dest[4] = x1;
		dest[5] = y1;
		dest[6] = z1;
		dest[7] = 0.0f;
		dest[8] = x2;
		dest[9] = y2;
		dest[10] = z2;
		dest[11] = 0.0f;
		dest[12] = -(x0*eyex + x1*eyey + x2*eyez);
		dest[13] = -(y0*eyex + y1*eyey + y2*eyez);
		dest[14] = -(z0*eyex + z1*eyey + z2*eyez);
		dest[15] = 1.0f;

		return dest;
	}



	/*
	 * float[] transpose
	 * Transposes a mat4 (flips the values over the diagonal)
	 *
	 * Params:
	 * mat - mat4 to transpose
	 * dest - Optional, mat4 receiving transposed values. If not specified result is written to mat
	 *
	 * Returns:
	 * dest is specified, mat otherwise
	 */
	public static float[] transpose(float[] mat) {
		float[]	dest = new float[16]; 
		dest[0] = mat[0];
		dest[1] = mat[4];
		dest[2] = mat[8];
		dest[3] = mat[12];
		dest[4] = mat[1];
		dest[5] = mat[5];
		dest[6] = mat[9];
		dest[7] = mat[13];
		dest[8] = mat[2];
		dest[9] = mat[6];
		dest[10] = mat[10];
		dest[11] = mat[14];
		dest[12] = mat[3];
		dest[13] = mat[7];
		dest[14] = mat[11];
		dest[15] = mat[15];
		return dest;
	};


	/*
	 * float[] inverse
	 * Calculates the inverse matrix of a mat4
	 *
	 * Params:
	 * mat - mat4 to calculate inverse of
	 * dest - Optional, mat4 receiving inverse matrix. If not specified result is written to mat
	 *
	 * Returns:
	 * dest is specified, mat otherwise
	 */
	public static float[] inverseMatrix(float[] mat) {

		float[] dest = new float[16];

		// Cache the matrix values (makes for huge speed increases!)
		float a00 = mat[0], a01 = mat[1], a02 = mat[2], a03 = mat[3];
		float a10 = mat[4], a11 = mat[5], a12 = mat[6], a13 = mat[7];
		float a20 = mat[8], a21 = mat[9], a22 = mat[10], a23 = mat[11];
		float a30 = mat[12], a31 = mat[13], a32 = mat[14], a33 = mat[15];

		float b00 = a00*a11 - a01*a10;
		float b01 = a00*a12 - a02*a10;
		float b02 = a00*a13 - a03*a10;
		float b03 = a01*a12 - a02*a11;
		float b04 = a01*a13 - a03*a11;
		float b05 = a02*a13 - a03*a12;
		float b06 = a20*a31 - a21*a30;
		float b07 = a20*a32 - a22*a30;
		float b08 = a20*a33 - a23*a30;
		float b09 = a21*a32 - a22*a31;
		float b10 = a21*a33 - a23*a31;
		float b11 = a22*a33 - a23*a32;

		// Calculate the determinant (inlined to avoid float-caching)
		float invDet = 1.0f/(b00*b11 - b01*b10 + b02*b09 + b03*b08 - b04*b07 + b05*b06);

		dest[0] = (a11*b11 - a12*b10 + a13*b09)*invDet;
		dest[1] = (-a01*b11 + a02*b10 - a03*b09)*invDet;
		dest[2] = (a31*b05 - a32*b04 + a33*b03)*invDet;
		dest[3] = (-a21*b05 + a22*b04 - a23*b03)*invDet;
		dest[4] = (-a10*b11 + a12*b08 - a13*b07)*invDet;
		dest[5] = (a00*b11 - a02*b08 + a03*b07)*invDet;
		dest[6] = (-a30*b05 + a32*b02 - a33*b01)*invDet;
		dest[7] = (a20*b05 - a22*b02 + a23*b01)*invDet;
		dest[8] = (a10*b10 - a11*b08 + a13*b06)*invDet;
		dest[9] = (-a00*b10 + a01*b08 - a03*b06)*invDet;
		dest[10] = (a30*b04 - a31*b02 + a33*b00)*invDet;
		dest[11] = (-a20*b04 + a21*b02 - a23*b00)*invDet;
		dest[12] = (-a10*b09 + a11*b07 - a12*b06)*invDet;
		dest[13] = (a00*b09 - a01*b07 + a02*b06)*invDet;
		dest[14] = (-a30*b03 + a31*b01 - a32*b00)*invDet;
		dest[15] = (a20*b03 - a21*b01 + a22*b00)*invDet;

		return dest;
	}

	
	
	public static float[] identity() {
		return new float[] {
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f };
	};
	
	
	/*
	 * float[] multiply
	 * Performs a matrix multiplication
	 *
	 * Params:
	 * mat - mat4, first operand
	 * mat2 - mat4, second operand
	 * dest - Optional, mat4 receiving operation result. If not specified result is written to mat
	 *
	 * Returns:
	 * dest if specified, mat otherwise
	 */
	public static float[] multiply(float[] mat, float[] mat2) {
		float[] dest = new float[16];

		// Cache the matrix values (makes for huge speed increases!)
		float a00 = mat[0], a01 = mat[1], a02 = mat[2], a03 = mat[3];
		float a10 = mat[4], a11 = mat[5], a12 = mat[6], a13 = mat[7];
		float a20 = mat[8], a21 = mat[9], a22 = mat[10], a23 = mat[11];
		float a30 = mat[12], a31 = mat[13], a32 = mat[14], a33 = mat[15];

		float b00 = mat2[0], b01 = mat2[1], b02 = mat2[2], b03 = mat2[3];
		float b10 = mat2[4], b11 = mat2[5], b12 = mat2[6], b13 = mat2[7];
		float b20 = mat2[8], b21 = mat2[9], b22 = mat2[10], b23 = mat2[11];
		float b30 = mat2[12], b31 = mat2[13], b32 = mat2[14], b33 = mat2[15];

		dest[0] = b00*a00 + b01*a10 + b02*a20 + b03*a30;
		dest[1] = b00*a01 + b01*a11 + b02*a21 + b03*a31;
		dest[2] = b00*a02 + b01*a12 + b02*a22 + b03*a32;
		dest[3] = b00*a03 + b01*a13 + b02*a23 + b03*a33;
		dest[4] = b10*a00 + b11*a10 + b12*a20 + b13*a30;
		dest[5] = b10*a01 + b11*a11 + b12*a21 + b13*a31;
		dest[6] = b10*a02 + b11*a12 + b12*a22 + b13*a32;
		dest[7] = b10*a03 + b11*a13 + b12*a23 + b13*a33;
		dest[8] = b20*a00 + b21*a10 + b22*a20 + b23*a30;
		dest[9] = b20*a01 + b21*a11 + b22*a21 + b23*a31;
		dest[10] = b20*a02 + b21*a12 + b22*a22 + b23*a32;
		dest[11] = b20*a03 + b21*a13 + b22*a23 + b23*a33;
		dest[12] = b30*a00 + b31*a10 + b32*a20 + b33*a30;
		dest[13] = b30*a01 + b31*a11 + b32*a21 + b33*a31;
		dest[14] = b30*a02 + b31*a12 + b32*a22 + b33*a32;
		dest[15] = b30*a03 + b31*a13 + b32*a23 + b33*a33;

		return dest;
	}

	
	/*
	 * float[] translate
	 * Translates a matrix by the given vector
	 *
	 * Params:
	 * mat - mat4 to translate
	 * vec - vec3 specifying the translation
	 * dest - Optional, mat4 receiving operation result. If not specified result is written to mat
	 *
	 * Returns:
	 * dest if specified, mat otherwise
	 */
	public static float[] translate(float[] mat, float[] vec) {
		float x = vec[0], y = vec[1], z = vec[2];

		float a00 = mat[0], a01 = mat[1], a02 = mat[2], a03 = mat[3];
		float a10 = mat[4], a11 = mat[5], a12 = mat[6], a13 = mat[7];
		float a20 = mat[8], a21 = mat[9], a22 = mat[10], a23 = mat[11];

        float[] dest = new float[16];

		dest[0] = a00;
		dest[1] = a01;
		dest[2] = a02;
		dest[3] = a03;
		dest[4] = a10;
		dest[5] = a11;
		dest[6] = a12;
		dest[7] = a13;
		dest[8] = a20;
		dest[9] = a21;
		dest[10] = a22;
		dest[11] = a23;

		dest[12] = a00*x + a10*y + a20*z + mat[12];
		dest[13] = a01*x + a11*y + a21*z + mat[13];
		dest[14] = a02*x + a12*y + a22*z + mat[14];
		dest[15] = a03*x + a13*y + a23*z + mat[15];
		return dest;
	}

	/*
	 * float[] scale
	 * Scales a matrix by the given vector
	 *
	 * Params:
	 * mat - mat4 to scale
	 * vec - vec3 specifying the scale for each axis
	 * dest - Optional, mat4 receiving operation result. If not specified result is written to mat
	 *
	 * Returns:
	 * dest if specified, mat otherwise
	 */
	public static float[] scale(float[] mat, float[] vec) {
		float x = vec[0], y = vec[1], z = vec[2];

        float[] dest = new float[16];

		dest[0] = mat[0]*x;
		dest[1] = mat[1]*x;
		dest[2] = mat[2]*x;
		dest[3] = mat[3]*x;
		dest[4] = mat[4]*y;
		dest[5] = mat[5]*y;
		dest[6] = mat[6]*y;
		dest[7] = mat[7]*y;
		dest[8] = mat[8]*z;
		dest[9] = mat[9]*z;
		dest[10] = mat[10]*z;
		dest[11] = mat[11]*z;
		dest[12] = mat[12];
		dest[13] = mat[13];
		dest[14] = mat[14];
		dest[15] = mat[15];
		return dest;
	}

	/*
	 * float[] rotate
	 * Rotates a matrix by the given angle around the specified axis
	 * If rotating around a primary axis (X,Y,Z) one of the specialized rotation functions should be used instead for performance
	 *
	 * Params:
	 * mat - mat4 to rotate
	 * angle - angle (in radians) to rotate
	 * axis - vec3 representing the axis to rotate around 
	 * dest - Optional, mat4 receiving operation result. If not specified result is written to mat
	 *
	 * Returns:
	 * dest if specified, mat otherwise
	 */
	public static float[] rotate(float[] mat, float angle, float[] axis) {
		float x = axis[0], y = axis[1], z = axis[2];
		float len = (float)Math.sqrt(x*x + y*y + z*z);
		if (len == 0) { return null; }
		if (len != 1.0) {
			len = 1.0f / len;
			x *= len; 
			y *= len; 
			z *= len;
		}

		float s = (float)Math.sin(angle);
		float c = (float)Math.cos(angle);
		float t = 1.0f-c;

		// Cache the matrix values (makes for huge speed increases!)
		float a00 = mat[0], a01 = mat[1], a02 = mat[2], a03 = mat[3];
		float a10 = mat[4], a11 = mat[5], a12 = mat[6], a13 = mat[7];
		float a20 = mat[8], a21 = mat[9], a22 = mat[10], a23 = mat[11];

		//float a30 = mat[12], a31 = mat[13], a32 = mat[14], a33 = mat[15];

		
		// Construct the elements of the rotation matrix
		float b00 = x*x*t + c, b01 = y*x*t + z*s, b02 = z*x*t - y*s;
		float b10 = x*y*t - z*s, b11 = y*y*t + c, b12 = z*y*t + x*s;
		float b20 = x*z*t + y*s, b21 = y*z*t - x*s, b22 = z*z*t + c;

        float[] dest = new float[16];

//		if(!dest) { 
//			dest = mat 
//		} else if(mat != dest) { // If the source and destination differ, copy the unchanged last row
			dest[12] = mat[12];
			dest[13] = mat[13];
			dest[14] = mat[14];
			dest[15] = mat[15];
//		}

		// Perform rotation-specific matrix multiplication
		dest[0] = a00*b00 + a10*b01 + a20*b02;
		dest[1] = a01*b00 + a11*b01 + a21*b02;
		dest[2] = a02*b00 + a12*b01 + a22*b02;
		dest[3] = a03*b00 + a13*b01 + a23*b02;

		dest[4] = a00*b10 + a10*b11 + a20*b12;
		dest[5] = a01*b10 + a11*b11 + a21*b12;
		dest[6] = a02*b10 + a12*b11 + a22*b12;
		dest[7] = a03*b10 + a13*b11 + a23*b12;

		dest[8] = a00*b20 + a10*b21 + a20*b22;
		dest[9] = a01*b20 + a11*b21 + a21*b22;
		dest[10] = a02*b20 + a12*b21 + a22*b22;
		dest[11] = a03*b20 + a13*b21 + a23*b22;

		//dest[12] = a03*b00 + a13*b01 + a23*b02;
		//dest[13] = a03*b10 + a13*b11 + a23*b12;
		//dest[14] = a03*b20 + a13*b21 + a23*b22;

		
		return dest;
	}

	
	public static float[] scaleMatrix(float[] s) {
		return Matrices.scale(identity(), s);
	}

	public static float[] rotation(float angle, float[] axis) {
		return Matrices.rotate(identity(), angle, axis);
	}

	public static float[] translation( float[] vec ) {
		return Matrices.translate(identity(), vec);
	}
	
	

	
	public static float[] transformVec4Mat4(float[] vec4,  float[] mat4) {
        float[] out = new float[4];

	    float x = vec4[0], y = vec4[1], z = vec4[2], w = vec4[3];
	    out[0] = mat4[0] * x + mat4[4] * y + mat4[8] * z + mat4[12] * w;
	    out[1] = mat4[1] * x + mat4[5] * y + mat4[9] * z + mat4[13] * w;
	    out[2] = mat4[2] * x + mat4[6] * y + mat4[10] * z + mat4[14] * w;
	    out[3] = mat4[3] * x + mat4[7] * y + mat4[11] * z + mat4[15] * w;
	    return out;
	}
	
	public static float[] transformVec3Mat4(float[] vec3,  float[] mat4) {
        float[] out = new float[3];
	    float x = vec3[0], y = vec3[1], z = vec3[2];
	    out[0] = mat4[0] * x + mat4[4] * y + mat4[8] * z ;
	    out[1] = mat4[1] * x + mat4[5] * y + mat4[9] * z ;
	    out[2] = mat4[2] * x + mat4[6] * y + mat4[10] * z ;
	    return out;
	}

	
}
