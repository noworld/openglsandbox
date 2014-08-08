precision mediump float;

const vec3 CONST_COLOR = vec3(0.0,0.3,0.6);

uniform vec3      u_LightPos;
uniform sampler2D u_Texture;
  
varying vec3  v_Position;		 // Interpolated position for this fragment.
varying vec3  v_Normal;
varying vec2  v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying float v_Transp;
varying float v_InitialHeight;
  
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
    diffuse = diffuse + 0.4;

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
	if(v_InitialHeight > -1.0) {
		vec4 color = (diffuse * texture2D(u_Texture, v_TexCoordinate));
		gl_FragColor = vec4(diffuse * color.rgb, v_Transp);
	} else {
		gl_FragColor = vec4(1.0,1.0,1.0,1.0);
	}
	
    //gl_FragColor = vec4(CONST_COLOR * diffuse, 0.95);
    
    
  }                                                                     	

