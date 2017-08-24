package com.practice.heliguang.airhockey.objects;

import com.practice.heliguang.airhockey.data.VertexArray;
import com.practice.heliguang.airhockey.programs.ColorShaderProgram;
import com.practice.heliguang.opengles2library.Gemometry;

import java.util.List;

/**
 * Created by heliguang on 2017/8/24.
 */

public class Puck {
    private static final int POSITION_COMPONECT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(
                new Gemometry.Cylinder(new Gemometry.Point(0f, 0f, 0f), radius, height),
                numPointsAroundPuck
        );

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONECT_COUNT,
                0
        );
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
