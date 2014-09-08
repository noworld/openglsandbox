uniform mat4 u_MVPMatrix;  
uniform vec2 u_Dimensions;
uniform vec2 u_LineVec;
uniform float u_Height;
		  			
attribute vec4 a_Position;
		
varying vec3 v_Position;		 		
		    
void main()                                                 	
{
                               
	gl_Position =  u_MVPMatrix * a_Position;  
	
	v_Position = a_Position.xyz;

}                                                          