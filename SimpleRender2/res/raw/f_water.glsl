precision mediump float;

const float       AMBIENT = 0.4;
const int         MAX_LIGHTS = 5;
uniform int       u_NumLights;
uniform sampler2D u_Texture;
uniform vec3[MAX_LIGHTS] u_LightPoses;
uniform vec4[MAX_LIGHTS] u_LightColors;
  
varying vec3  v_Position;		 // Interpolated position for this fragment.
varying vec3  v_Normal;
varying vec2  v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying float v_Transp;
varying float v_InitialHeight;
  
// The entry point for our fragment shader.
void main()                    		
{	        
	float totalDiffuse = 0.0;
	vec4 combinedCol = vec4(0.0,0.0,0.0,0.0);
	
	for(int i = 0; i < u_NumLights; i++) {
		vec3 u_LightPos = u_LightPoses[i];
		vec4 u_LightCol = u_LightColors[i];
		
		// Will be used for attenuation.
    	float distance = length(u_LightPos - v_Position);               
	
		// Get a lighting direction vector from the light to the vertex.
	    vec3 lightVector = normalize(u_LightPos - v_Position);       

		// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
		// pointing in the same direction then it will get max illumination.
	    float diffuse = max(dot(v_Normal, lightVector), 0.0);               	  		  													  
	
		// Add attenuation. 
	    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));
	    totalDiffuse = (totalDiffuse + diffuse)/2.0;
	    
	  	//Keep the running average
	    combinedCol.rgb = ((combinedCol.rgb * (float(i))) + (u_LightCol.rgb * diffuse)) / (float(i+1));
	    combinedCol.w =   ((combinedCol.w * (float(i))) + diffuse) / (float(i+1));   
	}

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
	vec4 texColor = texture2D(u_Texture, v_TexCoordinate);
	vec3 color = totalDiffuse * texColor.rgb;
	//vec3 color = (texColor.rgb);
	gl_FragColor = vec4(color, v_Transp);
	//gl_FragColor = vec4(0.0f, 0.4f, 0.8f, 1.0f);
    
  }                                                                     	

