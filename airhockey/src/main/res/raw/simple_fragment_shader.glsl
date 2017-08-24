precision mediump float; // 定义所有浮点数据类型的默认精度：lowp低精度 mediump中等精度 highp高精度

uniform vec4 u_Color;

void main() {
    gl_FragColor = u_Color;
}