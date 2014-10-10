uniform mat4 u_MVPMatrix;	       
uniform mat4 u_MVMatrix;
		  			
attribute vec4 a_Position; 							
attribute vec3 a_Normal; 
attribute vec2 a_TexCoordinate;		
		  
varying vec3 v_Position;     		          		
varying vec3 v_Normal;
varying vec2 v_TexCoordinate; 		
 
void main()                                                 	
{                                                         	
	v_Position = a_Position.xyz;      		
	v_TexCoordinate = a_TexCoordinate;                                      
    v_Normal = a_Normal;
	gl_Position = a_Position;                       	

}                                                          