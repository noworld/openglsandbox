precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.
  
varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec3 v_Normal;         	// Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.


// The entry point for our fragment shader.
void main()                    		
{                        
	float levels = 3.0;
	float scale = 1.0/levels;
	float intensity = 1.0;
	vec3 Kd = vec3(0.357,0.839,0.749);
	vec3 Ka = vec3(0.357,0.839,0.749);

	vec3 s = normalize(u_LightPos - v_Position);
	float cosine = max(0.0, dot(s, v_Normal));
	vec3 diffuse = Kd * floor(cosine * levels) * scale;
	
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate) * vec4((Ka + diffuse),1.0);                        		
  }                                                                     	

