struct wave {
	int wave_type; //0=linear, 1=radial
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
const int MAX_WAVES = 15;
const float Q_SCALE = 0.9;
const float K_EXP = 2.0;

uniform mat4      u_MVPMatrix;      		       
uniform mat4      u_MVMatrix; 
uniform mat4      u_NrmMatrix;  
uniform float     u_Time;
uniform vec4      u_WaterColor;
uniform int       u_NumWaves;

uniform wave[MAX_WAVES] u_Waves;
		  			
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;	
		  
varying vec3 v_Position;  
varying vec3 v_Normal;    		          		  
varying vec4 v_WaterColor;  
varying vec2 v_TexCoordinate;
		  
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
		
		//New sin function with exponent k for peaks
		//Don't really use on sin waves, but for texture waves
		height = height + 2.0 * u_Wave.amplitude * pow(((sin(angle)+1.0)/2.0), K_EXP);
		
		float normMult = K_EXP * u_Wave.frequency * u_Wave.amplitude * pow(((sin(angle)+1.0)/2.0), K_EXP-1.0) * cos(angle);
		float xDir = u_Wave.direction.x * normMult;
		float zDir = u_Wave.direction.z * normMult;
		
		//float xDir = K_EXP * u_Wave.direction.x * u_Wave.frequency * u_Wave.amplitude * pow(((sin(angle)+1.0)/2.0), K_EXP-1.0) * cos(angle);
		//float yDir = K_EXP * u_Wave.direction.y * u_Wave.frequency * u_Wave.amplitude * pow(((sin(angle)+1.0)/2.0), K_EXP-1.0) * cos(angle);
		//float zDir = K_EXP * u_Wave.direction.z * u_Wave.frequency * u_Wave.amplitude * pow(((sin(angle)+1.0)/2.0), K_EXP-1.0) * cos(angle);
		
		normal = normal + normalize(vec3(xDir,1.0,zDir));
	}	
	
	//normal = vec3(0.0, 1.0, 0.0);
	normal = normalize(normal);
	
	//Set the height on the point
	vec4 position = a_Position;
	position.y = position.y + height;	
	position.x = position.x;
	position.z = position.z;
                         
	//Translate to view space
	v_Position = vec3(u_MVMatrix * position);
	
	//v_Normal = vec3(u_MVMatrix * vec4(normal, 0.0));
	v_Normal = normal;
	
	v_WaterColor = u_WaterColor;
	v_TexCoordinate = a_TexCoordinate;
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * position;
	
}       
                                             