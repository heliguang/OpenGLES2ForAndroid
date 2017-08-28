package com.practice.heliguang.livewallpaper;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.practice.heliguang.opengles2library.OpenGLES20;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView;
    private LiveWallPaperRenderer renderer;

    private boolean rendererSet = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float previousX, previousY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event != null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    previousX = event.getX();
                    previousY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    final float deltaX = event.getX() - previousX;
                    final float deltaY = event.getY() - previousY;
                    previousX = event.getX();
                    previousY = event.getY();

                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleTouchDrag(deltaX, deltaY);
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

            renderer = new LiveWallPaperRenderer(this);
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
