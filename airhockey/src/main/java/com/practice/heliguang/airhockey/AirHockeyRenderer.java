package com.practice.heliguang.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.practice.heliguang.opengles2library.ShaderHelper;
import com.practice.heliguang.opengles2library.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by heliguang on 2017/8/21.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer{
    public static final String TAG = "AirHockeyRenderer";

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTE_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private final Context context;

    private int program;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PER_FLOAT;

    private int aColorLoacation;

    private static final String U_MATRIX = "u_Matrix";

    private final float[] projectionMatrix = new float[16];

    private int uMatrixLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // 逆时针顺序定义三角形的三个顶点，这种成为 卷曲顺序(winding order)。
                // 可优化性能、可以指出一个三角形属于任何给定物体的前面或后面
                // 定义三角扇形的顶点属性 X Y R G B
                    0,      0,   1f,   1f,   1f,
                -0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,
                 0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,
                 0.5f,   0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f,   0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,

                // 中间分界线
                -0.5f,     0f,   1f,   0f,   0f,
                 0.5f,     0f,   1f,   0f,   0f,

                // 两个木槌的位置
                   0f,  -0.4f,   0f,   0f,   1f,
                   0f,   0.4f,   1f,   0f,   0f
        };

        // 通过JAVA类，分配本地内存块
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder()) // 保证字节缓冲区按照本地字节序组织内容，和平台保持一直
                .asFloatBuffer();
        // 将Java数据复制到本地内存块中，使得OpenGL可以存取
        // 程序结束时，内存被释放掉
        // 大量ByteBuffer需要堆碎片化和内存管理：https://en.wikipedia.org/wiki/Memory_pool
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.d(TAG, "onSurfaceCreated()");

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);   // 当屏幕清空后，会显示这个颜色

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        ShaderHelper.validateProgram(program);  // 验证program，非必要

        glUseProgram(program);  // 告诉OpenGL绘制任何东西到屏幕使用该program

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLoacation = glGetAttribLocation(program, A_COLOR);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0); // 设置缓冲区指针位置
        glVertexAttribPointer(  // 设置OpenGL在vertexData中查找a_Position对应数据
                aPositionLocation,
                POSITION_COMPONENT_COUNT,   // 每个属性数据的计数，或者对于这个属性，有多少个分量与每一个顶点相关联
                GL_FLOAT,   // 数据类型
                false,  // 当数据为整形数据时，才有意义
                STRIDE,  // 当一个数组中存储多于一个属性时，才有意义
                vertexData);
        glEnableVertexAttribArray(aPositionLocation);  // 使能

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(
                aColorLoacation,
                COLOR_COMPONENT_COUNT,
                GL_FLOAT,
                false,
                STRIDE,
                vertexData
        );
        glEnableVertexAttribArray(aColorLoacation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Logger.d(TAG, "onSurfaceChanged()");

        glViewport(0, 0, width, height);    // 设置OpenGL可以用来渲染的surface大小

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;


        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    /*
    GLSurfaceView在一个单独的线程中调用渲染器的方法。
    默认情况下，GLSurfaceView以设备刷新频率不断的渲染，
    也可以配置为按请求渲染，方法为：glSurfaceView.setRenderMode(GLSurfaceView.DEBUG_LOG_GL_CALLS)
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        Logger.d(TAG, "onDrawFrame()");

        glClear(GL_COLOR_BUFFER_BIT);   // 擦除屏幕所有颜色，并使用glClearColor()定义的颜色填充整个屏幕

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 画桌子
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);   // 取6个数据画三角形

        // 画分界线
        glDrawArrays(GL_LINES, 6, 2);

        // 画木槌
        glDrawArrays(GL_POINTS, 8, 1);

        glDrawArrays(GL_POINTS, 9, 1);
    }
}
