struct wave {
	float amplitude;
	vec3  direction;
	float wavelength;
	float frequency;
	float speed;
	float phase_const;
	float time_scale;
	float phase_shift;
};

const int MAX_WAVES = 3;

uniform int       u_NumWaves;
uniform mat4      u_MVPMatrix;      		       
uniform mat4      u_MVMatrix;    
uniform sampler2D u_Texture;    
uniform float     u_Time;
uniform float     u_Transp;

uniform wave[MAX_WAVES] u_Waves;
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. 		
		  
varying vec3 v_Position;		// This will be passed into the fragment shader.   
varying vec3 v_Normal;    		          		  
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.   
varying float v_Transp;
varying vec3 otherNormal;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{	
	float height = 0.0;
	for(int i = 0; i < u_NumWaves; i++) {
		wave u_Wave = u_Waves[i];
		float phase = (u_Time * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
		float angle = (dot(u_Wave.direction.xz, a_Position.xz) * u_Wave.frequency) + phase;
		height = height + u_Wave.amplitude * sin(angle);		
	}	
	
	vec4 position = a_Position;
	position.y = height;	
                         
    // Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;   

	v_Position = vec3(u_MVMatrix * position);	
	vec3 normal = vec3(0.0, 1.0, 1.0);
	v_Normal = vec3(u_MVMatrix * vec4(normal, 0.0));	
	v_Transp = u_Transp;
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * position;   

}                                                          