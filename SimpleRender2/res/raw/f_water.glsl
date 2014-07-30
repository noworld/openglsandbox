precision mediump float;

uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.
uniform sampler2D u_NormalTexture;    // The input texture.
  
varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying vec3 v_Normal;
varying mat4 v_MVMatrix;
  
// The entry point for our fragment shader.
void main()                    		
{
	vec4 normalFromMap = texture2D(u_NormalTexture, v_TexCoordinate);
	
	//Swizzle because rgb = xyz and we want to map z to y
	vec3 normal_rgb = normalFromMap.rbg;	
	
	vec3 normal = vec3(v_MVMatrix * vec4(normal_rgb,0.0));
	                              
	// Will be used for attenuation.
    float distance = length(u_LightPos - v_Position);                  
	
	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - v_Position);       

	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(normal, lightVector), 0.0);               	  		  													  

	// Add attenuation. 
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));
    
    // Add ambient lighting
    diffuse = diffuse + 0.5;  

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
	vec4 color = (diffuse * texture2D(u_Texture, v_TexCoordinate));
	//vec4 color = diffuse * normalFromMap;
	
    gl_FragColor = color;  
    gl_FragColor.a = 1.0;                      		
  }                                                                     	

