uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.    
uniform sampler2D u_Texture;    // The input texture.
uniform sampler2D u_NormalTexture;    // The normal map input texture.   		
uniform float u_Time;
uniform vec3 u_EyePos;
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   							
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.      
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. 		
		  
varying vec3 v_Position;		// This will be passed into the fragment shader.       		          		  
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.   
varying vec3 v_Normal; 		
varying mat4 v_MVMatrix;
varying float v_Height;

const float c_TwoPi = 6.2831853072;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{

	float amplitude = 0.1;
	vec3 direction = vec3(0.0, 0.0, 1.0);
	float wavelength = 1.0;
	float freq = c_TwoPi/wavelength;
	float speed = 1.2;
	float phase_const = speed * freq;
	float time_stretch = 1.0;
	
	float phase = (u_Time * phase_const * time_stretch);
	
	//vec3 secondSamplePoint = vec3(a_Position) + (0.1 * normalize(u_EyePos - vec3(a_Position)));
	float angle1 = (dot(direction.xz, a_Position.xz) * freq) + phase;
	float partial_h_x = freq * direction.x * amplitude * cos(angle1);
	float partial_h_z = freq * direction.z * amplitude * cos(angle1);
	vec3 bitangent = vec3(1.0,partial_h_x,0.0);
	vec3 tangent = vec3(0.0,partial_h_z,1.0);

	float height1 = amplitude * sin(angle1);
	
	vec4 position = a_Position;
	position.y = height1;
	
	amplitude = 0.15;
	direction = vec3(1.0, 0.0, 0.75);
	wavelength = 4.0;
	freq = c_TwoPi/wavelength;
	speed = 0.75;
	phase_const = speed * freq;
	time_stretch = 0.75;
	
	phase = (u_Time * phase_const * time_stretch);
	
	angle1 = (dot(direction.xz, a_Position.xz) * freq) + phase;
	
	partial_h_x = freq * direction.x * amplitude * cos(angle1);
	partial_h_z = freq * direction.z * amplitude * cos(angle1);
	bitangent = bitangent + vec3(1.0,partial_h_x,0.0);
	tangent = tangent + vec3(0.0,partial_h_z,1.0);
	
	height1 = amplitude * sin(angle1);

	position.y = position.y + height1;
	v_Height = position.y;
                         
    // Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;   

	vec3 normal = cross(bitangent, tangent);
	v_Position = vec3(u_MVMatrix * position);
	//v_Normal = vec3(u_MVMatrix * vec4(normal, 0.0));
	v_Normal = normal;
	v_MVMatrix = u_MVMatrix;
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * position;   

}                                                          