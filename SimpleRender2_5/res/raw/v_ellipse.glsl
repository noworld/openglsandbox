
uniform mat4 u_VMatrix;
		  			
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
		
varying vec3 v_Position;
varying vec2 v_TexCoordinate;  	 		
		    
void main()                                                 	
{
                               
	gl_Position = a_Position;
	v_Position = a_Position.xyz;
	v_TexCoordinate = a_TexCoordinate;

}                                                          