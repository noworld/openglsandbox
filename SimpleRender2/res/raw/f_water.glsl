precision mediump float;

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

uniform vec3      u_LightPos;
uniform sampler2D u_Texture;
uniform wave      u_Wave;
  
varying vec3  v_Position;		 // Interpolated position for this fragment.
varying vec3  v_Normal;
varying vec2  v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying float v_TestValue;       // Test value passed from vertex shader
varying float v_Transp;
  
// The entry point for our fragment shader.
void main()                    		
{	                              
	// Will be used for attenuation.
    float distance = length(u_LightPos - v_Position);                  
	
	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - v_Position);       

	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(v_Normal, lightVector), 0.0);               	  		  													  

	// Add attenuation. 
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));
    
    // Add ambient lighting
    diffuse = diffuse + 0.5;  

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
	vec4 color = (diffuse * texture2D(u_Texture, v_TexCoordinate));
	
    gl_FragColor = color;
    gl_FragColor.a = v_Transp; 
    
  }                                                                     	

