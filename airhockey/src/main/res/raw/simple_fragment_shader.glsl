precision mediump float; // 定义所有浮点数据类型的默认精度：lowp低精度 mediump中等精度 highp高精度

varying vec4 v_Color;

void main() {
    gl_FragColor = v_Color;
}