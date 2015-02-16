precision mediump float;

const vec4 F_COLOR = vec4(0.5, 0.5, 0.5, 1.0); 

uniform sampler2D u_Texture;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

varying float v_fogFactor;

void main()                    		
{                              
    vec4 color = texture2D(u_Texture, v_TexCoordinate);
    gl_FragColor =  mix(F_COLOR, color, v_fogFactor);    
}                                                                     	

