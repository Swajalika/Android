package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.example.android.opengl.Node.TreeNode;

import android.opengl.GLES20;
import android.util.Log;


public class Square{
	
    private final String vertexShaderCode =
            
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
    static int NO_OF_ROOTCHILD=MyGLRenderer.number;					//root.count
    public static TreeNode root,subRoot;
    static int sizeSquareCoords;//8*root.count
    final int buffers[] = new int[2];
    float color[]=new float[4];
    
    public Square(TreeNode T) {
    	root=T;
    	sizeSquareCoords=8*NO_OF_ROOTCHILD;
    	float squareCoords[]=new float[sizeSquareCoords];
    	int k=0;
    	for(int j=0;j<=8*(root.count-1);j+=8){
	    	
    		squareCoords[j]=root.child[k].X;
    		squareCoords[j+1]=root.child[k].Y;
    		squareCoords[j+2]=root.child[k].X+ root.child[k].width;
    		squareCoords[j+3]= root.child[k].Y;
    		squareCoords[j+4]= root.child[k].X+ root.child[k].width;
    		squareCoords[j+5]= root.child[k].Y+ root.child[k].height;
    		squareCoords[j+6]= root.child[k].X;
    		squareCoords[j+7]= root.child[k].Y+ root.child[k].height;
	    	k++;
    	}
    	
    	
    	for(int i=0;i<sizeSquareCoords;i++){
			Log.i("TAG","squareCoords : "+squareCoords[i]);
			
    	}

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        												// (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             
        GLES20.glAttachShader(mProgram, vertexShader);   
        GLES20.glAttachShader(mProgram, fragmentShader); 
        GLES20.glLinkProgram(mProgram);                  
        
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        
        
    }
    
    public void draw(float[] mvpMatrix) {
    	
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        //first vertex buffer object
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
                
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        for(int start=0;start< 4*NO_OF_ROOTCHILD;){
        	for(int k=0;k<3;k++)
            	color[k] = (float) Math.random();
        	color[3]=1.0f;
        	//j++;
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, start, 4);
        	start+=4;
        }
        
      // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }

}
