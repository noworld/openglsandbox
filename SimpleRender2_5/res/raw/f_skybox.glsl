precision mediump float;
								
uniform samplerCube u_Texture;
varying vec3 v_TexCoordinate;
  
// The entry point for our fragment shader.
void main()                    		
{                              
    gl_FragColor = textureCube(u_Texture, v_TexCoordinate);          		
}                                                                     	

