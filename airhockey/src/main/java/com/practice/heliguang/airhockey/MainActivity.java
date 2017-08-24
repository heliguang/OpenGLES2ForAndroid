package com.practice.heliguang.airhockey;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.practice.heliguang.opengles2library.OpenGLES20;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView;
    private AirHockeyRenderer renderer;

    private boolean rendererSet = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event != null) {
                // 归一化设备坐标 [-1, 1]
                final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleTouchPress(normalizedX, normalizedY);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleTouchDrag(normalizedX, normalizedY);
                        }
                    });
                }

                return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (OpenGLES20.supportsEs2(this)) {
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);

            renderer = new AirHockeyRenderer(this);
            glSurfaceView.setRenderer(renderer);
            glSurfaceView.setOnTouchListener(onTouchListener);

            rendererSet = true;

            setContentView(glSurfaceView);
        } else {
            Logger.e(TAG, "This device doesn't support OpenGL ES 2.0.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) glSurfaceView.onResume();
    }
}
