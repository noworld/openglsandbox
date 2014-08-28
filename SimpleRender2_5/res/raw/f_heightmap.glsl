precision mediump float;

uniform sampler2D u_Texture;
uniform vec2 u_Dimensions;
uniform vec2 u_LineVec;
uniform float u_Height;
  
varying vec3 v_Position;
varying vec2 v_TexCoordinate;
  
void main()                    		
{                              
    vec3 color = texture2D(u_Texture, v_TexCoordinate).rgb;
    vec3 height = vec3(u_Height);
    gl_FragColor = vec4((color + height), 1.0);               		
}                                                                     	

