precision mediump float;

uniform sampler2D u_TextureUnit;    // 一个二维纹理数组

varying vec2 v_TextureCoordinates;

void main() {
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);  // 读入纹理中特定坐标处的颜色值
}