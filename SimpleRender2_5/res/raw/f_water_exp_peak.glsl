precision mediump float;

const float     KA = 1.0;
const float     KD = 2.0;
const float     KS = 3.0;
const float     SPECULAR_EXP = 5.0;

const int       MAX_LIGHTS = 1;

uniform int       			u_NumLights;
uniform vec4[MAX_LIGHTS]	u_LightPositons;
uniform vec3                u_EyePos;
uniform sampler2D			u_Texture;
uniform mat4      			u_NrmMatrix;  
uniform highp mat4      	u_MVMatrix; 
  
varying vec3  v_Position;		// Interpolated position for this fragment.
varying vec3  v_Normal;			// Normal for the wave surface as calculated in the vertex shader
varying vec4  v_WaterColor;		// Watercolor from vertex shader
varying vec2 v_TexCoordinate;

vec3 ads(vec4 position, vec3 norm) {

	vec4 lightpos = u_LightPositons[0];

	vec3 s;
	if(lightpos.w == 0.0) {
		s = normalize(vec3(lightpos));
	} else {
		s = normalize(vec3(lightpos - v_Position));
	}
	
	vec3 v = normalize(vec3(-position));
	vec3 r = reflect(-s,norm);
	
	return vec3(v_WaterColor * (KA + 
								KD * max(dot(s, norm), 0.1) +
								KS * pow(max(dot(r,v), 0.1), SPECULAR_EXP)));

}
  
void main()                    		
{	        
	float totalDiffuse = 0.0;
	float totalSpecular = 0.0;
	
	vec4 txnormal = texture2D(u_Texture, v_TexCoordinate);  
	txnormal = txnormal.xzyw;
	vec3 eyenorm = normalize(vec3(u_NrmMatrix * txnormal));
	//vec3 eyenorm = normalize(vec3(u_NrmMatrix * vec4(v_Normal, 1.0)));
	vec4 eyepos = u_MVMatrix * vec4(v_Position, 1.0);
	
	vec3 color = ads(eyepos, eyenorm);
	
	//vec3 color = light * v_WaterColor.rgb;
	gl_FragColor = vec4(color, v_WaterColor.a);
	//gl_FragColor = normal;

  }                                                                     	

