package com.practice.heliguang.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.practice.heliguang.airhockey.objects.Mallet;
import com.practice.heliguang.airhockey.objects.Puck;
import com.practice.heliguang.airhockey.objects.Table;
import com.practice.heliguang.airhockey.programs.ColorShaderProgram;
import com.practice.heliguang.airhockey.programs.TextureShaderProgram;
import com.practice.heliguang.opengles2library.Geometry;
import com.practice.heliguang.opengles2library.MatrixHelper;
import com.practice.heliguang.opengles2library.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
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

    private boolean redMalletPressed = false;  // 红色木槌是否被按下
    private boolean blueMalletPressed = false;  // 蓝色木槌是否被按下
    private Geometry.Point redMalletPosition; // 标记红色木槌位置
    private Geometry.Point blueMalletPosition; // 标记蓝色木槌位置

    private final float[] invertedViewProjectionMatrix = new float[16]; // 反转矩阵，

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point previousRedMalletPosition;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        Geometry.Sphere blueMalletBoundingSphere = new Geometry.Sphere(
                new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z),
                mallet.height / 2f
        );
        blueMalletPressed = Geometry.intersects(blueMalletBoundingSphere, ray);


        Geometry.Sphere redMalletBoundingSphere = new Geometry.Sphere(
                new Geometry.Point(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z),
                mallet.height / 2f
        );
        redMalletPressed = Geometry.intersects(redMalletBoundingSphere, ray);
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        // 得到世界空间坐标
        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        // 撤销透视除法影响
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(
                nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]
        );
        Geometry.Point farPointRay = new Geometry.Point(
                farPointWorld[0], farPointWorld[1], farPointWorld[2]
        );

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));

        Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
        if (redMalletPressed) {
            previousRedMalletPosition = redMalletPosition;
            redMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            farBound + mallet.radius,
                            0f - mallet.radius)
            );
            float distance = Geometry.vectorBetween(redMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousRedMalletPosition, redMalletPosition);
            }
        }
        if (blueMalletPressed) {
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            0f + mallet.radius,
                            nearBound - mallet.radius)
            );

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
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

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        redMalletPosition = new Geometry.Point(0f, mallet.height / 2f, -0.4f);

        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
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
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        puckPosition = puckPosition.translate(puckVector);
        if (puckPosition.x < leftBound + puck.radius ||
                puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius ||
                puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );
        puckVector = puckVector.scale(0.99f);

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z);
        colorProgram.useProgram();
        colorProgram.setUniform(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniform(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
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
