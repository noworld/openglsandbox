uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;    		
		  			
attribute vec4 a_Position;							

varying vec3 v_TexCoordinate;		
		  
// The entry point for our vertex shader.  
void main()                                                 	
{                                                              		
	// Pass through the texture coordinate.
	v_TexCoordinate = a_Position;                                      
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position =  u_MVPMatrix * a_Position;                       	

}                                                          