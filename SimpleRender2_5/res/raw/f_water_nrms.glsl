precision mediump float;

const float     KA = 0.9;
const float     KD = 0.5;
const float     KS = 0.9;
const float     SPECULAR_EXP = 5.0;

uniform vec3				u_LightVec;
uniform vec3                u_EyePos;
uniform sampler2D			u_Texture;
uniform mat4      			u_NrmMatrix;  
uniform highp mat4      	u_MVMatrix; 
uniform vec4      			u_WaterColor;
uniform vec4      			u_SkyColor;
  
varying vec3 v_Position;
varying vec2 v_TexCoordinate;

vec3 ads(vec4 color, vec4 position, vec3 norm) {

	vec3 s = normalize(u_LightVec);
	vec3 v = normalize(vec3(-position));
	vec3 r = reflect(s,norm);

	return vec3(color * (KA + KD * max(dot(s, norm), 0.0) + KS * pow(max(dot(r,v), 0.0), SPECULAR_EXP)));

}
  
void main()                    		
{	        
	vec4 txnormal = texture2D(u_Texture, v_TexCoordinate).xzyw;  
	vec3 tn = normalize(vec3(txnormal));
	float mixer = tn.y / (tn.x + tn.y + tn.z);
	
	vec3 eyenorm = normalize(vec3(u_NrmMatrix * txnormal));
	vec4 eyepos = u_MVMatrix * vec4(v_Position, 1.0);
	vec3 watercolor = ads(u_WaterColor, eyepos, eyenorm);
	vec3 skycolor = ads(u_SkyColor, eyepos, eyenorm);
	gl_FragColor = vec4(mix(watercolor,skycolor,mixer), u_WaterColor.a);
	//gl_FragColor = vec4(watercolor, u_WaterColor.a);
	//gl_FragColor = vec4(v_Position, 1.0);

  }                                                                     	

