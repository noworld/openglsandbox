uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.    
uniform sampler2D u_Texture;    // The input texture.
uniform sampler2D u_NormalTexture;    // The normal map input texture.   		
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   							
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.      
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. 		
		  
varying vec3 v_Position;		// This will be passed into the fragment shader.       		          		  
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.   
varying vec3 v_Normal; 		
varying mat4 v_MVMatrix;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{

	// Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;   
                           
	// Transform the vertex into eye space. 	
	//Bump!
	//Grab the normal alpha from the texture
	float disp = texture2D(u_NormalTexture, a_TexCoordinate).a;
	//Copy in the position
	vec4 position = a_Position;
	//Displace the y
	position.y = position.y + (disp - 0.5);

	v_Position = vec3(u_MVMatrix * position);
	v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
	v_MVMatrix = u_MVMatrix;
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * position;   
	
	//To prevent z-clipping
	 //gl_Position.z = 0.0;	//causes artifacts
}                                                          