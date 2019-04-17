precision mediump float;
uniform sampler2D s_texture;
varying vec2 textureCoordinate;

void main(){
    gl_FragColor = texture2D(s_texture, textureCoordinate);
}