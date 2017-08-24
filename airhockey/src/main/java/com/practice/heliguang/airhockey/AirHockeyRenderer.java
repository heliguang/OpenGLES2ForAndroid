package com.practice.heliguang.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.practice.heliguang.airhockey.objects.Mallet;
import com.practice.heliguang.airhockey.objects.Puck;
import com.practice.heliguang.airhockey.objects.Table;
import com.practice.heliguang.airhockey.programs.ColorShaderProgram;
import com.practice.heliguang.airhockey.programs.TextureShaderProgram;
import com.practice.heliguang.opengles2library.MatrixHelper;
import com.practice.heliguang.opengles2library.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by heliguang on 2017/8/21.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer{
    public static final String TAG = "AirHockeyRenderer";

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.d(TAG, "onSurfaceCreated()");

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);   // 当屏幕清空后，会显示这个颜色

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.mipmap.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Logger.d(TAG, "onSurfaceChanged()");

        glViewport(0, 0, width, height);    // 设置OpenGL可以用来渲染的surface大小

        // 生成投影矩阵，45度视野，视锥体从z值为-1位置开始，在z值-10位置结束
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);  // 设置视图矩阵
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

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniform(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniform(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniform(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionTableInScene() {
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }
}
