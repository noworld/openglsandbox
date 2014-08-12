precision mediump float;

const float     AMBIENT = 0.3;
const float     DIFFUSE_REFLECTIVITY = 1.0;
const float     DIFFUSE_INTENSITY = 5.0;
const float     ALPHA = 1.0;
const int       MAX_LIGHTS = 5;

uniform int       			u_NumLights;
uniform vec3[MAX_LIGHTS]	u_LightPositons;
  
varying vec3  v_Position;		// Interpolated position for this fragment.
varying vec3  v_Normal;			// Normal for the wave surface as calculated in the vertex shader
varying vec3  v_WaterColor;		// Watercolor from vertex shader
  
void main()                    		
{	        
	float totalDiffuse = 0.0;
	
	for(int i = 0; i < u_NumLights; i++) {
		vec3 u_LightPos = u_LightPositons[i];
		
		// Will be used for attenuation.
    	float distance = length(u_LightPos - v_Position);               
	
		// Get a lighting direction vector from the light to the vertex.
	    vec3 lightVector = normalize(u_LightPos - v_Position);       

		// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
		// pointing in the same direction then it will get max illumination.
	    float diffuse = DIFFUSE_INTENSITY * DIFFUSE_REFLECTIVITY * max(dot(lightVector, v_Normal), 0.0);               	  		  													  
	
		// Add attenuation. 
	    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));
	    totalDiffuse = totalDiffuse + diffuse;
	}
	
	//totalDiffuse = totalDiffuse + AMBIENT;
	vec3 color = totalDiffuse * v_WaterColor;
	gl_FragColor = vec4(color, ALPHA);
	//gl_FragColor = vec4(v_WaterColor, ALPHA);
	//gl_FragColor = vec4(v_Normal, ALPHA);

  }                                                                     	

