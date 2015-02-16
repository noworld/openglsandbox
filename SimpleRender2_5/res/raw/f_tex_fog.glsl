precision mediump float;

const vec4 F_COLOR = vec4(0.5, 0.5, 0.5, 1.0); 
const float LOG2 = 1.442695;
const float F_DENSITY = 0.1;

uniform sampler2D u_Texture;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()                    		
{                              
	float z = gl_FragCoord.z / gl_FragCoord.w;
	float fogFactor = exp2(-F_DENSITY * 
					   F_DENSITY * 
					   z * 
					   z * 
					   LOG2 );
	fogFactor = clamp(fogFactor, 0.0, 1.0);     
	
    vec4 color = texture2D(u_Texture, v_TexCoordinate);
    gl_FragColor =  mix(F_COLOR, color, fogFactor);    
                        		
}                                                                     	

