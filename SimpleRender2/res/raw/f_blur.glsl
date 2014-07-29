precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.
  
varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec3 v_Normal;         	// Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

varying highp vec2 blurCoordinates[5];
 
void main()
{
	lowp vec4 sum = vec4(0.0);
	sum += texture2D(u_Texture, blurCoordinates[0]) * 0.204164;
	sum += texture2D(u_Texture, blurCoordinates[1]) * 0.304005;
	sum += texture2D(u_Texture, blurCoordinates[2]) * 0.304005;
	sum += texture2D(u_Texture, blurCoordinates[3]) * 0.093913;
	sum += texture2D(u_Texture, blurCoordinates[4]) * 0.093913;
	gl_FragColor = sum;
}