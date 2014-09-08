precision highp float;

uniform sampler2D u_Texture;
uniform vec2 u_Dimensions;
uniform vec2[2] u_LineVec;
uniform float u_Height;
  
varying vec3 v_Position;
varying vec2 v_TexCoordinate;
  
void main()                    		
{                           
	//float red = (v_Position.x + 1.0)/2.0; 
	float red = 0.3;
	//float green = (v_Position.y + 1.0)/2.0;
	float green = 0.3;
	
	if(abs(v_Position.y - u_LineVec[0].y) < 0.001 || abs(v_Position.x - u_LineVec[0].x) < 0.00099) {
	    red = 1.0;
	}
	
	if(abs(v_Position.y - u_LineVec[1].y) < 0.001 || abs(v_Position.x - u_LineVec[1].x) < 0.00099) {
	    green = 1.0;
	}
	
	gl_FragColor = vec4(red,green,0.3,1.0);              		
}                                                                     	

