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
const float K_EXP = 2.0;
const float Q_SCALE = 0.75;

uniform mat4      u_MVPMatrix;      		       
uniform mat4      u_MVMatrix;       
uniform float     u_Time;
uniform vec3      u_WaterColor;
uniform int       u_NumWaves;

uniform wave[MAX_WAVES] u_Waves;
		  			
attribute vec4 a_Position;
		  
varying vec3 v_Position;  
varying vec3 v_Normal;    		          		  
varying vec3 v_WaterColor;  
		  
void main()                                                 	
{	
	float height = 0.0;
	float xDisp = 0.0;
	float zDisp = 0.0;
	vec3 normal = vec3(0.0, 0.0, 0.0);
	float combinedAmp = 0.0;
	
	//Add up the height of all the waves
	for(int i = 0; i < u_NumWaves; i++) {		
		wave u_Wave = u_Waves[i];
		float q = (1.0 / (u_Wave.frequency * u_Wave.amplitude)) * Q_SCALE;
		float phase = (u_Time * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
		float angle = dot(u_Wave.frequency * u_Wave.direction.xz, a_Position.xz) + phase;
		float sinA = sin(angle);
		float cosA = cos(angle);
		xDisp = xDisp + ((q * u_Wave.amplitude) * u_Wave.direction.x * cosA);		
		zDisp = zDisp + ((q * u_Wave.amplitude) * u_Wave.direction.z * cosA);

		height = height + u_Wave.amplitude * sinA;
		
		float wa = u_Wave.frequency * u_Wave.amplitude;
		float xDir = u_Wave.direction.x * wa * cosA;
		float zDir = u_Wave.direction.z * wa * cosA;
		float yDir = 1.0 - (q * wa * sinA);
		normal = normal + normalize(vec3(-xDir,yDir,-zDir));
	}	
	
	//Set the height on the point
	vec4 position = a_Position;
	position.y = height;	
	position.x = position.x + xDisp;
	position.z = position.z + zDisp;

	v_Position = vec3(u_MVMatrix * position);
	v_Normal = normal;
	v_WaterColor = u_WaterColor;

	gl_Position = u_MVPMatrix * position;
	
}       
                                             