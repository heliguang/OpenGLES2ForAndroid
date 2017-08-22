attribute vec4 a_Position;  // 属性，每个顶点设置一个 属性有默认值 0 0 0 1
attribute vec4 a_Color;

varying vec4 v_Color;   // varying 特殊的数据类型，会将赋的值进行混合，并把混合后的指发送到片段着色器

void main() {
    v_Color = a_Color;

    gl_Position = a_Position;
    gl_PointSize = 10.0;    // gl_Position:当OpenGL把一个点分解为片段的时候，会生产一些片段以gl_Position为中心边长为gl_Position的四边形
}