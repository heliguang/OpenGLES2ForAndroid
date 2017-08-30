package com.practice.heliguang.livewallpaper.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.practice.heliguang.livewallpaper.LiveWallPaperRenderer;
import com.practice.heliguang.opengles2library.OpenGLES20;

/**
 * Created by heliguang on 2017/8/30.
 */

public class GLLiveWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }
    public class GLEngine extends Engine {
        private static final String TAG = "GLEngine";

        private WallpaperGLSurfaceView glSurfaceView;
        private LiveWallPaperRenderer particlesRenderer;
        private boolean rendererSet;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLLiveWallpaperService.this);

            particlesRenderer = new LiveWallPaperRenderer(GLLiveWallpaperService.this);

            if (OpenGLES20.supportsEs2(GLLiveWallpaperService.this)) {
                glSurfaceView.setEGLContextClientVersion(2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    glSurfaceView.setPreserveEGLContextOnPause(true);
                }
                glSurfaceView.setRenderer(particlesRenderer);
                rendererSet = true;
            } else {
                /*
                 * This is where you could create an OpenGL ES 1.x compatible
                 * renderer if you wanted to support both ES 1 and ES 2. Since
                 * we're not doing anything, the app will crash if the device
                 * doesn't support OpenGL ES 2.0. If we publish on the market, we
                 * should also add the following to AndroidManifest.xml:
                 *
                 * <uses-feature android:glEsVersion="0x00020000"
                 * android:required="true" />
                 *
                 * This hides our app from those devices which don't support OpenGL
                 * ES 2.0.
                 */
                Toast.makeText(GLLiveWallpaperService.this,
                        "This device does not support OpenGL ES 2.0.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }
        @Override
        public void onOffsetsChanged(final float xOffset, final float yOffset,
                                     float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            // TODO:设置壁纸随屏幕切换移动时，红米3上出现绘制偏差的问题
//            glSurfaceView.queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    particlesRenderer.handleOffsetsChanged(xOffset, yOffset);
//                }
//            });
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestroy();
        }
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";
            WallpaperGLSurfaceView(Context context) {
                super(context);
            }
            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }
            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
