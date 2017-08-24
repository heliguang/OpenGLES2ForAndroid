uniform mat4 u_Matrix;

attribute vec4 a_Position;  // 属性，每个顶点设置一个 属性有默认值 0 0 0 1

void main() {
    gl_Position = u_Matrix * a_Position;
}