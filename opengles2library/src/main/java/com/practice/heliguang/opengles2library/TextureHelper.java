package com.practice.heliguang.opengles2library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by heliguang on 2017/8/23.
 */

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];

        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) return 0;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            Logger.e(TAG, "Resource ID " + resourceId + " could not be decoded.");

            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // 设置纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR); // 缩小情况，使用三线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);   // 放大过滤设置为双线性过滤

        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);    // OpenGL读取bitmap定义的位图数据，并赋值到当前绑定的纹理对象

        bitmap.recycle();

        glGenerateMipmap(GL_TEXTURE_2D);    // 生成MIP贴图，设置生成所有必要的级别

        glBindTexture(GL_TEXTURE_2D, 0);    // 传递0，解除与当前纹理绑定。防止调用其他纹理方法改变这个纹理

        return textureObjectIds[0];
    }
}
