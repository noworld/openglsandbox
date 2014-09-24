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

const float       c_BlockDisp = 0.25;

uniform wave      u_Wave;
uniform float     u_Time;
uniform vec4      u_WaterColor;
uniform int       u_NumWaves;
uniform float     u_XPos;
uniform float     u_ZPos;
uniform float     u_MipMult;


uniform mat4 u_MVPMatrix;	       
uniform mat4 u_MVMatrix;
uniform mat4 u_MMatrix;
uniform mat4 u_VMatrix;
uniform mat4 u_VPMatrix;
uniform mat4 u_NrmMatrix;
		  			
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;	
		  
varying vec2 v_TexCoordinate;
varying vec3 v_Position;     		          		
varying vec3 v_Normal;
varying vec4 v_WaterColor;
 
void main()                                                 	
{          

	float time = u_Time;
	//float time = 1.0;
    float height = 0.0;
	float xDisp = 0.0;
	float zDisp = 0.0;
	vec3 normal = vec3(0.0, 0.0, 0.0);
	vec4 newPos = u_MMatrix * a_Position;

	//Sum the positions
	float q = (1.0 / (u_Wave.frequency * u_Wave.amplitude * float(u_NumWaves)));
	//vec3 normDir = normalize(u_Wave.direction);
	vec3 normDir = u_Wave.direction;
	float phase = (time * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
	float angle = (u_Wave.frequency * dot(normDir.xz, newPos.xz)) + phase;
	float sinA = sin(angle);
	float cosA = cos(angle);
	
	//Sums for the position, y up
	xDisp = xDisp + ((q * u_Wave.amplitude) * normDir.x * cosA);		
	zDisp = zDisp + ((q * u_Wave.amplitude) * normDir.z * cosA);
	height = height + (u_Wave.amplitude * sinA);    
	
	//Set the height on the point
	vec4 position = newPos;
	position.y = height;	
	position.x = position.x + xDisp;
	position.z = position.z + zDisp;
	
	//Sum the normals
	normDir = u_Wave.direction;
	
	q = (1.0 / (u_Wave.frequency * u_Wave.amplitude * float(u_NumWaves)));
	phase = (time * u_Wave.phase_const * u_Wave.time_scale) + u_Wave.phase_shift;
	angle = (u_Wave.frequency * dot(normDir.xz, position.xz)) + phase;
	sinA = sin(angle);
	cosA = cos(angle);
	
	float wa = u_Wave.frequency * u_Wave.amplitude;
	
	//Directions for the normals, y up
	float xDir = normDir.x * wa * cosA;
	float zDir = normDir.z * wa * cosA;
	float yDir = q * wa * sinA;
	
	//Sum the normals
	normal = normal + vec3(xDir,yDir,zDir);
	
    normal = vec3(-normal.x, 1.0 - normal.y, -normal.z);
	v_WaterColor = u_WaterColor;
	//v_TexCoordinate = vec4(u_MMatrix * vec4(a_TexCoordinate.s, 0.0, a_TexCoordinate.t, 1.0)).xz;
	float xTxDisp = u_XPos * c_BlockDisp;
	float zTxDisp = u_ZPos * c_BlockDisp;
	v_TexCoordinate = vec2((a_TexCoordinate.s + xTxDisp) * u_MipMult, (a_TexCoordinate.t + zTxDisp) * u_MipMult);
	
	v_Position = vec3(u_VMatrix * a_Position);
	v_Normal = normalize(vec3(u_NrmMatrix * vec4(normal,1.0)));
	
	gl_Position = u_MVPMatrix * a_Position;
	//gl_Position = u_VPMatrix * position;                    	

}                                                          