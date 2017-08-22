precision mediump float; // 定义所有浮点数据类型的默认精度：lowp低精度 mediump中等精度 highp高精度

uniform vec4 u_Color;   // uniform会让每个顶点使用同一个值，除非我们再次改变它 uniform没有默认值

void main() {
    gl_FragColor = u_Color;
}