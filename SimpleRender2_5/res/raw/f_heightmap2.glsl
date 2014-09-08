precision highp float;

uniform vec2 u_Dimensions;
uniform vec2[2] u_LineVec;
uniform float u_Height;
uniform sampler2D u_Texture;

varying vec2 v_TexCoordinate;  
varying vec3 v_Position;
  
void main()                    		
{                           
	vec2 comparison = v_Position.xy - u_LineVec[0];
	float dotp = dot(comparison, u_LineVec[1]);
	
	vec4 texColor = texture2D(u_Texture, v_TexCoordinate);  
	
	if(dotp > 0.0) {
		gl_FragColor = vec4(u_Height) + texColor;
	} else {
		gl_FragColor = texColor;
	}	

}                                                               	

