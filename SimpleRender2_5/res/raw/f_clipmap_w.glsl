precision mediump float;

const float     AMBIENT = 0.6;
const float     DIFFUSE_REFLECTIVITY = 1.0;
const float     DIFFUSE_INTENSITY = 5.0;
const float     SPECULAR_REFLECTIVITY = 1.0;
const float     SPECULAR_INTENSITY = 3.0;
const float     SPECULAR_K = 5.0;
const int       MAX_LIGHTS = 5;

uniform sampler2D u_Texture;


uniform vec3 u_LightDir;
uniform vec3 u_EyePos;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;
varying vec4 v_WaterColor; 

void main()                    		
{     

	float totalDiffuse = 0.0;
	float totalSpecular = 0.0;
	
	vec3 u_LightPos = u_LightDir;
	vec3 s = normalize(u_LightPos - v_Position);
	vec3 v = normalize(-v_Position);
	vec3 r = reflect(-s, v_Normal);
	float distance = length(u_LightPos - v_Position);
	float sdotn = max(dot(s, v_Normal), 0.0);
	float diffuse = DIFFUSE_INTENSITY * DIFFUSE_REFLECTIVITY * sdotn;
	float specular = 0.0;
	if(sdotn > 0.0) {
	    specular = SPECULAR_INTENSITY * SPECULAR_REFLECTIVITY * pow(max(dot(r,v),0.0), SPECULAR_K);
	}

	float attenuation = (1.0 / (1.0 + (0.25 * distance)));
	
    totalDiffuse = totalDiffuse + (diffuse * attenuation);
    totalSpecular = totalSpecular + (specular);
	
	float light = totalDiffuse + AMBIENT + totalSpecular;
	vec3 color = light * v_WaterColor.rgb;
	
	gl_FragColor = vec4(color, v_WaterColor.a);                     
    //gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
    //gl_FragColor = v_WaterColor;
    //gl_FragColor = vec4(0.0, 0.3, 0.6, 1.0);                      		
}                                                                     	

