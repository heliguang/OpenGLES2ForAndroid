package com.practice.heliguang.airhockey;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.practice.heliguang.opengles2library.OpenGLES20;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView;
    private AirHockeyRenderer renderer;

    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (OpenGLES20.supportsEs2(this)) {
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);

            renderer = new AirHockeyRenderer(this);
            glSurfaceView.setRenderer(renderer);

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
