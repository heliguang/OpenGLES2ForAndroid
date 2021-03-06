package com.practice.heliguang.airhockey.programs;

import android.content.Context;

import com.practice.heliguang.opengles2library.ShaderHelper;
import com.practice.heliguang.opengles2library.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by heliguang on 2017/8/24.
 */

public class ShaderProgram {
    public static final String U_MATRIX = "u_Matrix";
    public static final String U_TEXTURE_UNIT = "u_TextureUnit";

    public static final String A_POSITION = "a_Position";
    public static final String A_COLOR = "a_Color";
    public static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    public static final String U_COLOR = "u_Color";

    public final int program;

    public ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        );
    }

    public void useProgram() {
        glUseProgram(program);
    }
}
