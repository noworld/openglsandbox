precision highp float;

uniform vec2 u_Dimensions;
uniform vec2[2] u_LineVec;
uniform float u_Height;
  
varying vec3 v_Position;
  
void main()                    		
{                           
	vec2 comparison = v_Position.xy - u_LineVec[0];
	float dotp = dot(comparison, u_LineVec[1]);
	
	if(dotp > 0.0) {
		gl_FragColor = vec4(1.0,1.0,1.0,1.0) * u_Height;
	} else {
		gl_FragColor = vec4(0.0,0.0,0.0,u_Height);
	}	
	
	//gl_FragColor = vec4(u_Height,u_Height,u_Height,u_Height);
}                                                               	

