precision mediump float;
								
uniform samplerCube u_Texture;
varying vec3 v_TexCoordinate;
  
// The entry point for our fragment shader.
void main()                    		
{                              
    vec3 cube = vec3(textureCube(u_Texture, v_TexCoordinate));
    gl_FragColor = vec4(cube, 1.0);                		
}                                                                     	

