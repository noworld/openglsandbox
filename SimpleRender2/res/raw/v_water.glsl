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

const float CONST_TIME = 1.0;
const int MAX_WAVES = 3;
const float K_EXP = 2.0;

uniform float     u_InitialHeight;
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
varying float v_InitialHeight;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{	
	float height = 0.0;
	vec3 normal = vec3(0.0, 0.0, 0.0);
	//Add up the height of all the waves
	for(int i = 0; i < u_NumWaves; i++) {		
		wave u_Wave = u_Waves[i];
		float phase = (u_Time * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
		//float phase = (CONST_TIME * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
		float angle = (dot(u_Wave.direction.xz, a_Position.xz) * u_Wave.frequency) + phase;
		height = height + u_Wave.amplitude * sin(angle);
		float xDir = u_Wave.direction.x * cos(angle);
		float yDir = (cos(2.0 * angle)+1.0)/2.0;
		float zDir = u_Wave.direction.z * cos(angle);
		normal = normal + normalize(vec3(xDir,1.0,zDir));
	}	
	
	//Set the height on the point
	vec4 position = a_Position;
	position.y = height;	
	position.x = position.x + u_InitialHeight;
	position.z = position.z + u_InitialHeight;
                         
    // Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;   

	//Translate to view space
	v_Position = vec3(u_MVMatrix * position);
	
	//v_Normal = vec3(u_MVMatrix * vec4(normal, 0.0));
	v_Normal = normal;
	v_Transp = u_Transp;
	v_InitialHeight = u_InitialHeight;
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * position;
	
}       
                                             