uniform mat4 u_Matrix;

attribute vec3 a_Position;

varying vec3 v_Position;

void main() {
    v_Position = a_Position;
    v_Position.z = -v_Position.z;

    gl_Position = u_Matrix * vec4(a_Position, 1.0);
    gl_Position = gl_Position.xyww; // 确保天空盒每一部分都将位于归一化设备坐标系的远平面以上已经场景其他一起的后面
}