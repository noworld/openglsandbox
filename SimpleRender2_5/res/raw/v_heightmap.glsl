uniform mat4 u_MVPMatrix;     		       
uniform mat4 u_MVMatrix;    		
		  			
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
		
varying vec3 v_Color;			 		
		    
void main()                                                 	
{
                               
	gl_Position =  u_MVPMatrix * a_Position;  
	
	vec3 color = vec3(1.0);
	
	//0,0 / 1,0 / 0,1 / 1,1
	color.r = a_TexCoordinate.s;
	color.g = a_TexCoordinate.t;
	color.b = 1.0 - abs(a_TexCoordinate.s - a_TexCoordinate.t);
	
	v_Color = color;                	

}                                                          