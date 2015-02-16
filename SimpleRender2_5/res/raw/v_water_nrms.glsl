uniform mat4      u_MVPMatrix;      		       
uniform mat4      u_MVMatrix; 
uniform mat4      u_NrmMatrix;  
uniform float     u_Time;
uniform vec4      u_WaterColor;
uniform vec4      u_SkyColor;
uniform vec3	  u_LightVec;
		  			
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;	
		  
varying vec3 v_Position;    		          		  
varying vec2 v_TexCoordinate;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{	
	//v_Position = vec3(u_MVMatrix * a_Position);
	v_Position = vec3(a_Position);
	v_TexCoordinate = a_TexCoordinate;
	gl_Position = u_MVPMatrix * a_Position;
}       
                                             