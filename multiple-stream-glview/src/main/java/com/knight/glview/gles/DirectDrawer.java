package com.knight.glview.gles;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;


import com.knight.glview.R;
import com.knight.glview.util.GlUtil;
import com.knight.glview.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class DirectDrawer {

    private Context mContext;


    // 顶点缓存
    private FloatBuffer vertexBuffer;
    // 纹理坐标映射缓存
    private FloatBuffer mTextureCoordsBuffer;
    // 绘制顺序缓存
    private ShortBuffer drawListBuffer;
    // OpenGL 可执行程序
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mMVPMatrixHandle;

    // 绘制顶点的顺序
    private short drawOrder[] = {0, 2, 1, 0, 3, 2};

    // 每个顶点的坐标数
    private final int COORDS_PER_VERTEX = 2;

    // 每个坐标数4 bytes，那么每个顶点占8 bytes
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float mVertices[] = new float[8];

    private float mTextureCoords[] = new float[8];
    private float mTextHeightRatio = 0.05f;

    // 需要绘制的纹理
    private int texture;
    public float[] mMVP = new float[16];

    // 判断是否为背部摄像头
    private boolean isBackCamera = true;


    public void setBackCamera(boolean b) {
        isBackCamera = b;
    }

    public void resetMatrix() {
        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }


    public void calculateMatrix(RectF rectF, float screenWidth, float screenHeight) {
        Matrix.setIdentityM(mMVP, 0);
        float scaleX = 1f / 4f;
        float scaleY = 1f / 4f;
        float ratioX = (rectF.left - .5f * (1 - scaleX) * screenWidth) / rectF.width();
        float ratioY = (rectF.top - .5f * (1 + scaleY) * screenHeight) / rectF.height();
        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);
        Matrix.translateM(mMVP, 0, ratioX * 2, ratioY * 2, 0f);
    }


    public DirectDrawer(int texture, Context context) {
        mContext = context;
        String vertextShader = TextResourceReader.readTextFileFromResource(mContext
                , R.raw.video_vertex_shader);
        String fragmentShader;
        if (texture == 1) {
            fragmentShader = TextResourceReader.readTextFileFromResource(mContext
                    , R.raw.video_normal_fragment_shader);
        } else {
            fragmentShader = TextResourceReader.readTextFileFromResource(mContext
                    , R.raw.video_state_fragment_shader);
        }
        // 创建 vertex shader和fragment shader 并将其添加到shader进行编译
        mProgram = GlUtil.createProgram(vertextShader, fragmentShader);

        if (mProgram == 0) {
            throw new RuntimeException("Unable to create program");
        }

        // 获取指向vertex shader的成员vPosition的句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GlUtil.checkLocation(mPositionHandle, "vPosition");

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GlUtil.checkLocation(mTextureCoordHandle, "inputTextureCoordinate");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GlUtil.checkLocation(mMVPMatrixHandle, "uMVPMatrix");


        this.texture = texture;


        setTexCoords();

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }

    private boolean isFromCamera = true;

    public void setFromCamera(boolean b) {
        isFromCamera = b;
    }

    int point = 5;

    public void draw() {
        if (isBackCamera) {
            mMVP[point] = Math.abs(mMVP[point]);
        } else {
            mMVP[point] = -Math.abs(mMVP[point]);
        }

        // 将program添加到OpenGL ES环境中
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (isFromCamera) {
            // initialize vertex byte buffer for shape coordinates
            updateVertices();
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        } else {
            RotateVertices();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        }
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mTextureCoordsBuffer);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    public static void mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float[] mout) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        mout[0] = 2.0f / r_l;
        mout[1] = 0.0f;
        mout[2] = 0.0f;
        mout[3] = 0.0f;

        mout[4] = 0.0f;
        mout[5] = 2.0f / t_b;
        mout[6] = 0.0f;
        mout[7] = 0.0f;

        mout[8] = 0.0f;
        mout[9] = 0.0f;
        mout[10] = -2.0f / f_n;
        mout[11] = 0.0f;

        mout[12] = tx;
        mout[13] = ty;
        mout[14] = tz;
        mout[15] = 1.0f;
    }

    public void rotateMat4() {
        mMVP[5] = -mMVP[5];
    }

    public void updateVertices() {
        final float w = 1.0f;
        final float h = 1.0f;
        mVertices[0] = -w;
        mVertices[1] = h;
        mVertices[2] = -w;
        mVertices[3] = -h;
        mVertices[4] = w;
        mVertices[5] = -h;
        mVertices[6] = w;
        mVertices[7] = h;
        vertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mVertices);
        vertexBuffer.position(0);
    }

    public void RotateVertices() {
        final float w = 1.0f;
        final float h = 1.0f;
        mVertices[6] = -w;
        mVertices[7] = h;
        mVertices[0] = -w;
        mVertices[1] = -h;
        mVertices[2] = w;
        mVertices[3] = -h;
        mVertices[4] = w;
        mVertices[5] = h;
        vertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mVertices);
        vertexBuffer.position(0);
    }

    public void setTexCoords() {
        mTextureCoords[0] = 0;
        mTextureCoords[1] = 1 - mTextHeightRatio;
        mTextureCoords[2] = 1;
        mTextureCoords[3] = 1 - mTextHeightRatio;
        mTextureCoords[4] = 1;
        mTextureCoords[5] = 0 + mTextHeightRatio;
        mTextureCoords[6] = 0;
        mTextureCoords[7] = 0 + mTextHeightRatio;
        mTextureCoordsBuffer = ByteBuffer.allocateDirect(mTextureCoords.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mTextureCoords);
        mTextureCoordsBuffer.position(0);
    }
}
