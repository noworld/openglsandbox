precision mediump float;

uniform sampler2D u_Texture;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;
varying vec4 v_WaterColor; 

void main()                    		
{                              
    //gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
    gl_FragColor = v_WaterColor;
    //gl_FragColor = vec4(0.0, 0.3, 0.6, 1.0);                      		
}                                                                     	

