package com.practice.heliguang.livewallpaper.objects;

import com.practice.heliguang.livewallpaper.data.VertexArray;
import com.practice.heliguang.livewallpaper.programs.SkyboxShaderProgram;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by heliguang on 2017/8/25.
 */

public class Skybox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox() {
        vertexArray = new VertexArray(
                new float[]{
                        -1, 1, 1,
                        1, 1, 1,
                        -1, -1, 1,
                        1, -1, 1,
                        -1, 1, -1,
                        1, 1, -1,
                        -1, -1, -1,
                        1, -1, -1
                }
        );

        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[]{
                        // 前面
                        1, 3, 0,
                        0, 3, 2,

                        // 后面
                        4, 6, 5,
                        5, 6, 7,

                        // 左面
                        0, 2, 4,
                        4, 2, 6,

                        // 右面
                        5, 7, 1,
                        1, 7, 3,

                        // 顶部
                        5, 1, 4,
                        4, 1, 0,

                        // 底部
                        6, 2, 7,
                        7, 2, 3
                });
        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                skyboxShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
    }
}
