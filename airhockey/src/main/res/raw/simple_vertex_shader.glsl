attribute vec4 a_Position;  // 属性，每个顶点设置一个 属性有默认值 0 0 0 1

void main() {
    gl_Position = a_Position;
    gl_PointSize = 10.0;    // gl_Position:当OpenGL把一个点分解为片段的时候，会生产一些片段以gl_Position为中心边长为gl_Position的四边形
}